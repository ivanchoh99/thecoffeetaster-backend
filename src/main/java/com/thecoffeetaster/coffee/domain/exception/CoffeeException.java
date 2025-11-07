package com.thecoffeetaster.coffee.domain.exception;

import org.springframework.http.HttpStatus;

public class CoffeeException extends RuntimeException {

    public CoffeeException(String message) {
        super(message);
    }
}
