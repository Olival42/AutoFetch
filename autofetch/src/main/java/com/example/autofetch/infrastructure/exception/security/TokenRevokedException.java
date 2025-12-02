package com.example.autofetch.infrastructure.exception.security;

public class TokenRevokedException extends RuntimeException {

    public TokenRevokedException(String message) {
        super(message);
    }

}
