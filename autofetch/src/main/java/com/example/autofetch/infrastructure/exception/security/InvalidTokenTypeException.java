package com.example.autofetch.infrastructure.exception.security;

public class InvalidTokenTypeException extends RuntimeException {

    public InvalidTokenTypeException(String message) {
        super(message);
    }

}
