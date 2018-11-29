package org.emerald.comicapi.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.emerald.comicapi.service.FileDetector;
import org.emerald.comicapi.validator.annotation.MongoId;
import org.emerald.comicapi.validator.annotation.UniqueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.multipart.MultipartFile;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.validator.annotation.Archive;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UniqueEntityValidator implements ConstraintValidator<UniqueEntity,Object> {

    @Autowired
    MongoTemplate mongoTemplate;

    private List<String> uniqueFields;
    private Class<?> entityClass;

    @Override
    public void initialize(UniqueEntity constraintAnnotation) {
        uniqueFields = Arrays.asList(constraintAnnotation.uniqueFields());
        entityClass = constraintAnnotation.entityClass();
    }

    @Override
    public boolean isValid(Object entityForm, ConstraintValidatorContext context) {
        try {
            Class<?> formClass = entityForm.getClass();
            for (String fieldName : uniqueFields) {
                char fistChar = Character.toUpperCase(fieldName.charAt(0));
                String restOfString = fieldName.substring(1);
                String methodName = String.format("get%c%s", fistChar, restOfString);
                Method getMethod = formClass.getMethod(methodName);
                Object value = getMethod.invoke(entityForm);

                if (value == null) return true;
            }

            Field[] entityDeclaredFields = entityClass.getDeclaredFields();
            Constructor<?> constructor = entityClass.getConstructor(entityForm.getClass());
            Object entityInstance = constructor.newInstance(entityForm);

            List<String> notInUniqueFields = new ArrayList<>();
            for (Field f : entityDeclaredFields) {
                String fieldName = f.getName();
                if (!uniqueFields.contains(fieldName))
                    notInUniqueFields.add(fieldName);
            }

            ExampleMatcher matcher = ExampleMatcher
                    .matchingAll()
                    .withIgnorePaths(notInUniqueFields.stream().toArray(String[]::new));
            Example<?> example = Example.of(entityInstance, matcher);

            Query query = new Query(Criteria.byExample(example));
            return !mongoTemplate.exists(query, entityClass);
        }
        catch (
            NoSuchMethodException     |
            IllegalAccessException    |
            InvocationTargetException |
            InstantiationException ex)
        {
            throw new RuntimeException(ex.getClass().getName());
        }
    }
}