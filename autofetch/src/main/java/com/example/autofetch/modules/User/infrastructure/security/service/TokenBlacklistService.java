package com.example.autofetch.modules.User.infrastructure.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    @Autowired
    private StringRedisTemplate redis;

    public void blacklistToken(String token, long expirationInSeconds) {
        redis.opsForValue().set(token, "blacklisted");
        redis.expire(token, java.time.Duration.ofSeconds(expirationInSeconds));
    }

    public boolean isTokenBlacklisted(String token) {
        return redis.hasKey(token);
    }
}
