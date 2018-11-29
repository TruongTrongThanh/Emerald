package org.emerald.comicapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileDetector {
    String detectExtension(MultipartFile file) throws IOException;
    String detectMimeType(MultipartFile file) throws IOException;
    String detectMimeType(File file) throws IOException;
    boolean isImage(File file) throws IOException;
}
