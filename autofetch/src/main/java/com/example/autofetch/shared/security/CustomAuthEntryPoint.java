package com.example.autofetch.shared.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.autofetch.shared.ApiResponse;
import com.example.autofetch.shared.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint{

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

            ErrorResponse error = ErrorResponse.builder()
                .error("INVALID_TOKEN")
                .message("Missing, invalid or expired token")
                .build();

            ApiResponse<?> body = ApiResponse.builder()
                .success(false)
                .data(null)
                .error(error)
                .build();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            String json = objectMapper.writeValueAsString(body);
            response.getWriter().write(json);
    }
}
