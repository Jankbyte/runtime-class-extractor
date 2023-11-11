package ru.jankbyte.classextractor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.List;

public class AgentLoader {
    private static final Logger logger = Logger.getLogger(
        AgentLoader.class.getName());

    public static void premain(String agentArgs,
            Instrumentation instrumentation) throws Exception {
        logger.log(Level.ALL, "Class-extractor success loaded as javaagent");
        InterceptorDescriptor descriptor = getDescriptor(agentArgs);
        ClassFileTransformer transformer =
            new ClassExtractorTransformer(descriptor);
        instrumentation.addTransformer(transformer);
    }

    private static InterceptorDescriptor getDescriptor(String propsPath)
            throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(propsPath));
        String saveDirectory = props.getProperty("saveDirectory");
        String classNames = props.getProperty("extractClassPatterns");
        if ((saveDirectory == null || saveDirectory.isEmpty()) ||
                (classNames == null || classNames.isEmpty())) {
            throw new IllegalArgumentException(
                "Save directory or class names is empty");
        }
        List<String> patterns = Stream.of(classNames.split("&&"))
            .map(String::trim).toList();
        File saveDir = new File(saveDirectory);
        saveDir.mkdirs();
        return new InterceptorDescriptor() {
            @Override
            public List<String> getExtractClassPatterns() {
                return patterns;
            }

            @Override
            public File getSaveDirectory() {
                return saveDir;
            }
        };
    }
}
