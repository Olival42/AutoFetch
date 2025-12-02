package com.example.autofetch.modules.User.infrastructure.security.infrastructure;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.autofetch.infrastructure.exception.security.MissingTokenException;
import com.example.autofetch.modules.User.infrastructure.security.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenConsistencyFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.startsWith("/auth/logout")) {
            chain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        Cookie[] cookies = request.getCookies();

        if (authorization != null && cookies != null) {

            String accessToken = authorization.replace("Bearer ", "");
            String refreshToken = null;

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }

            if (refreshToken != null) {
                String accessSub = jwtService.getSubject(accessToken);
                String refreshSub = jwtService.getSubject(refreshToken);

                if (!accessSub.equals(refreshSub)) {
                    throw new AccessDeniedException("Tokens belong to different users");
                }
            } else {
                throw new MissingTokenException("Refresh token is missing.");
            }
        }

        chain.doFilter(request, response);
    }
}
