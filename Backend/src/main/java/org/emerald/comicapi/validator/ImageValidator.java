package org.emerald.comicapi.validator;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.service.FileDetector;
import org.emerald.comicapi.validator.annotation.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;

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