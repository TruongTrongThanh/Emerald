package org.emerald.comicapi.validator;

import java.io.IOException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.tika.Tika;
import org.emerald.comicapi.service.FileDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.validator.annotation.Image;

public class ImageValidator implements ConstraintValidator<Image,MultipartFile> {
    
    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    FileDetector fileDetector;

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file != null) {
            String contentType;
            contentType = detectFile(file);
            return globalVariable.getAcceptImageTypes().contains(contentType);
        }
        return true;
    }

    private String detectFile(MultipartFile file) {
        try {
            return fileDetector.detectMimeType(file);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Something wrong in ImageValidator");
        }
    }
}