package org.emerald.comicapi.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.emerald.comicapi.service.FileDetector;
import org.emerald.comicapi.validator.annotation.MongoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.validator.annotation.Archive;

public class MongoIdValidator implements ConstraintValidator<MongoId,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || ObjectId.isValid(value);
    }
}