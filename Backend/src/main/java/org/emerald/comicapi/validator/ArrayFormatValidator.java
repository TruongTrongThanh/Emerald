package org.emerald.comicapi.validator;

import org.emerald.comicapi.validator.annotation.ArrayFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ArrayFormatValidator implements ConstraintValidator<ArrayFormat,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            Pattern regex = Pattern.compile("^(\\w+(,?)\\w+)+$", Pattern.CASE_INSENSITIVE);
            return regex.matcher(value).find();
        }
        return true;
    }
}
