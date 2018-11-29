package org.emerald.comicapi.service;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.emerald.comicapi.config.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileDetectorImpl implements FileDetector {
    private final Tika tika = new Tika();

    @Autowired
    GlobalVariable globalVariable;

    @Override
    public String detectExtension(MultipartFile file) throws IOException {
        try {
            String contentType = tika.detect(file.getBytes());
            MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(contentType);
            return mimeType.getExtension();
        }
        catch (MimeTypeException ex) {
            return null;
        }
    }

    @Override
    public String detectMimeType(MultipartFile file) throws IOException {
        return tika.detect(file.getBytes());
    }

    @Override
    public String detectMimeType(File file) throws IOException {
        return tika.detect(file);
    }

    @Override
    public boolean isImage(File file) throws IOException {
        return globalVariable.getAcceptImageTypes().contains(detectMimeType(file));
    }
}
