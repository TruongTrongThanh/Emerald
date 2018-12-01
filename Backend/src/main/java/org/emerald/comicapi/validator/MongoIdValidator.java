package org.emerald.comicapi.validator;

import org.bson.types.ObjectId;
import org.emerald.comicapi.validator.annotation.MongoId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MongoIdValidator implements ConstraintValidator<MongoId,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || ObjectId.isValid(value);
    }
}