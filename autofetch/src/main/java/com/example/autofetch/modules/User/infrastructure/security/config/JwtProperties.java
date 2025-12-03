package com.example.autofetch.modules.User.infrastructure.security.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private Duration accessTokenExpiration;
    private Duration refreshTokenExpiration;
}
