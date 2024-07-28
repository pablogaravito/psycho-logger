package com.pablogb.psychologger.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id, Class<?> entity) {
        super("El " + filterEntityName(entity.getSimpleName().toLowerCase()) + " con ID '" + id + "' no existe en nuestros registros");
    }

    private static String filterEntityName(String entityName) {
        return entityName.replace("entity", "");
    }
}
