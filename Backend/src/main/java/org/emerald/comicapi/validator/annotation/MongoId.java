package org.emerald.comicapi.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.emerald.comicapi.validator.MongoIdValidator;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MongoIdValidator.class)
public @interface MongoId {
    String message() default "Invalid ID format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}