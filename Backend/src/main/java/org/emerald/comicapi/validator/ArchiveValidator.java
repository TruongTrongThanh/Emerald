package org.emerald.comicapi.validator;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.service.FileDetector;
import org.emerald.comicapi.validator.annotation.Archive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ArchiveValidator implements ConstraintValidator<Archive,MultipartFile> {
    
    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    FileDetector fileDetector;

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (multipartFile != null) {
            String type;
            try {
                type = fileDetector.detectMimeType(multipartFile);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Zip's not working");
            }
            return globalVariable.getAcceptArchiveType().contains(type);
        }
        return true;
    }
}