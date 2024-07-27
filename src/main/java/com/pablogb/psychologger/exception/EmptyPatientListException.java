package com.pablogb.psychologger.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class EmptyPatientListException extends RuntimeException {
    public EmptyPatientListException() {
        super("You provided no patients for this session.");
    }
}
