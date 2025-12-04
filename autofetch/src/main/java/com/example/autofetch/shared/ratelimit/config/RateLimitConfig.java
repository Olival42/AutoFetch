package com.example.autofetch.shared.ratelimit.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;

@Configuration
public class RateLimitConfig {


    @Bean
    public ProxyManager<byte[]> proxyManager(RedisClient redisClient) {
        return LettuceBasedProxyManager
                .builderFor(redisClient)
                .build();
    }

    @Bean
    public Map<String, BucketConfiguration> rateLimitByRoute() {
        Map<String, BucketConfiguration> limits = new HashMap<>();

        limits.put("/auth/login", BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(5)
                                .refillIntervally(5, Duration.ofMinutes(1))
                                .build()
                )
                .build()
        );

        limits.put("/auth/forgot-password", BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(5)
                                .refillIntervally(5, Duration.ofMinutes(1))
                                .build()
                )
                .build()
        );

        limits.put("/auth/reset-password", BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(5)
                                .refillIntervally(5, Duration.ofMinutes(1))
                                .build()
                )
                .build()
        );

        limits.put("DEFAULT", BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(100)
                                .refillGreedy(100, Duration.ofMinutes(1))
                                .build()
                )
                .build()
        );

        return limits;
    }
}
