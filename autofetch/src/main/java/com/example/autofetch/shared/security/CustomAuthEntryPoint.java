package com.example.autofetch.shared.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.autofetch.shared.ApiResponse;
import com.example.autofetch.shared.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        String message = "Authentication failed.";
        String code = "UNAUTHORIZED";

        Throwable cause = authException.getCause();

        if (cause instanceof JwtException jwtEx &&
                jwtEx.getMessage().toLowerCase().contains("expired")) {
            message = "Token is expired.";
            code = "TOKEN_EXPIRED";
        }

        else if (cause instanceof BadJwtException) {
            message = "Invalid or malformed token.";
            code = "INVALID_TOKEN";
        }

        else if (cause instanceof JwtException) {
            message = "Could not validate token.";
            code = "INVALID_TOKEN";
        }

        else if (authException instanceof BadCredentialsException) {
            message = "Invalid credentials.";
            code = "INVALID_CREDENTIALS";
        }

        else if (authException.getMessage() != null &&
                authException.getMessage().contains("Full authentication is required")) {
            message = "Missing authentication token.";
            code = "TOKEN_MISSING";
        }

        ErrorResponse error = ErrorResponse.builder()
                .error(code)
                .message(message)
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .data(null)
                .error(error)
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
