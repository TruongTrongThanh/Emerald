package org.emerald.comicapi.validator.annotation;

import org.emerald.comicapi.validator.UniqueEntityValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEntityValidator.class)
public @interface UniqueEntity {
    String message() default "This one has been taken";
    String[] uniqueFields();
    Class<?> entityClass();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}