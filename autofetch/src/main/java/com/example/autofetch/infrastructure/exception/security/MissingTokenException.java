package com.example.autofetch.infrastructure.exception.security;

public class MissingTokenException extends RuntimeException {

    public MissingTokenException(String message) {
        super(message);
    }

}
