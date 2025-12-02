package com.example.autofetch.infrastructure.exception.security;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }

}
