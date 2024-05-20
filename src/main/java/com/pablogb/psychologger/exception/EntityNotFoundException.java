package com.pablogb.psychologger.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id, Class<?> entity) {
        super("The " + filterEntityName(entity.getSimpleName().toLowerCase()) + " with id '" + id + "' does not exist in our records");
    }

    private static String filterEntityName(String entityName) {
        return entityName.replace("entity", "");
    }
}
