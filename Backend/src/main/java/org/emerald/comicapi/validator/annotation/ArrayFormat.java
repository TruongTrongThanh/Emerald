package org.emerald.comicapi.validator.annotation;

import org.emerald.comicapi.validator.ArrayFormatValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ArrayFormatValidator.class)
public @interface ArrayFormat {
    String message() default "Invalid array format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}