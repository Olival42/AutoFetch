package com.example.autofetch.modules.User.infrastructure.security.infrastructure;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.autofetch.modules.User.infrastructure.security.service.TokenBlacklistService;
import com.example.autofetch.shared.ApiResponse;
import com.example.autofetch.shared.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenBlacklistFilter extends OncePerRequestFilter{

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                ErrorResponse error = ErrorResponse.builder()
                    .error("TOKEN_REVOKED")
                    .message("This token has been revoked and can no longer be used.")
                    .build();
            
                ApiResponse<?> apiResponse = ApiResponse.builder()
                        .success(false)
                        .data(null)
                        .error(error)
                        .build();

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

}
