package org.emerald.comicapi.validator;

import org.emerald.comicapi.validator.annotation.JsonArray;
import org.json.JSONArray;
import org.json.JSONException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class JsonArrayValidator implements ConstraintValidator<JsonArray,String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            try {
                new JSONArray(value);
            }
            catch (JSONException e) {
                return false;
            }
        }
        return true;
    }
}