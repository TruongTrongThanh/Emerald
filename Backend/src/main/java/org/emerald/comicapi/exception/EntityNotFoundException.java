package org.emerald.comicapi.exception;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {
    static final long serialVersionUID = 1L;

    @Getter
    private Class<?> entityClass;

    public EntityNotFoundException(Class<?> entityClass) {
        this.entityClass = entityClass;
    }
}