package com.sigl.sigl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Define que, ao lançar esta exceção, o status HTTP será 404 Not Found
public class ResourceNotFoundException extends RuntimeException {

    // Construtor que aceita uma mensagem e a passa para a superclasse RuntimeException
    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }
}