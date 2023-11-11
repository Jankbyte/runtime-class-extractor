package ru.jankbyte.classextractor;

import java.util.List;
import java.io.File;

public interface InterceptorDescriptor {
    List<String> getExtractClassPatterns();
    File getSaveDirectory();
}
