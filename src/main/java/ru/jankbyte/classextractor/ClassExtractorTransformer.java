package ru.jankbyte.classextractor;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.util.List;
import java.io.IOException;
import java.io.FileOutputStream;

public class ClassExtractorTransformer implements ClassFileTransformer {
    private static final Logger logger = Logger.getLogger(
            ClassExtractorTransformer.class.getName());

    private final String extractDirAbsolutePath;
    private final List<String> classPatters;

    public ClassExtractorTransformer(InterceptorDescriptor descriptor) {
        this.extractDirAbsolutePath = descriptor.getSaveDirectory()
            .getAbsolutePath();
        this.classPatters = descriptor.getExtractClassPatterns();
    }

    @Override
    public byte[] transform(Module module, ClassLoader loader,
            String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        String javaClassName = className.replaceAll("/", ".");
        try {
            logger.log(Level.INFO, "Processing class: %s"
                .formatted(javaClassName));
            if (!matchingClassName(javaClassName)) {
                return classfileBuffer;
            }
            String filePath = new StringBuilder(extractDirAbsolutePath)
                .append("/").append(className)
                .append(".class").toString();
            saveClassToFile(filePath, classfileBuffer);
        } catch (Exception ex) {
            logger.log(Level.INFO, "Cant extract class: %s (%s)"
                .formatted(javaClassName, ex.getMessage()));
        }
        return classfileBuffer;
    }

    private void saveClassToFile(String filePath, byte[] classfileBuffer)
            throws IOException {
        if (filePath.contains("/")) {
            createPackageDirs(filePath);
        }
        logger.log(Level.INFO, "Save class to file: %s".formatted(filePath));
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(classfileBuffer);
        } catch (IOException ex) {
            throw ex;
        }
    }

    private boolean matchingClassName(String className) {
        for (String pattern : classPatters) {
            boolean isMatch = className.matches(pattern);
            if (isMatch) {
                return true;
            }
        }
        return false;
    }

    private void createPackageDirs(String classPath) {
        if (!classPath.endsWith(".class")) {
            return;
        }
        String classesDir = classPath.replaceAll(
            "/[\\w\\d$]+\\.class$", "");
        new File(classesDir).mkdirs();
    }
}
