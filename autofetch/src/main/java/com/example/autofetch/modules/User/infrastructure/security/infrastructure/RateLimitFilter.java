package com.example.autofetch.modules.User.infrastructure.security.infrastructure;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.autofetch.shared.ApiResponse;
import com.example.autofetch.shared.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private ProxyManager<byte[]> proxyManager;

    @Autowired
    private Map<String, BucketConfiguration> rateLimitByRoute;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String rawRoute = request.getRequestURI();

        String route;
        if (rawRoute.startsWith("/auth/login")) {
            route = "/auth/login";
        } else if (rawRoute.startsWith("/auth/forgot-password")) {
            route = "/auth/forgot-password";
        } else if (rawRoute.startsWith("/auth/reset-password")) {
            route = "/auth/reset-password";
        } else {
            route = "DEFAULT";
        }

        BucketConfiguration bucketConfiguration = rateLimitByRoute.get(route);

        String redisKey = "rate-limit:" + route + ":" + resolveKey(request);
        byte[] key = redisKey.getBytes();

        Bucket bucket = proxyManager.builder()
                .build(key, () -> bucketConfiguration);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        ErrorResponse error = ErrorResponse.builder()
                .error("RATE_LIMIT_EXCEEDED")
                .message("Too many requests on route: " + route)
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .error(error)
                .build();

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                objectMapper.writeValueAsString(apiResponse));
    }

    private String resolveKey(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
