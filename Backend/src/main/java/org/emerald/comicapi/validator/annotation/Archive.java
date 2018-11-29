package org.emerald.comicapi.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.emerald.comicapi.validator.ArchiveValidator;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ArchiveValidator.class)
public @interface Archive {
    String message() default "Invalid archive file format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}