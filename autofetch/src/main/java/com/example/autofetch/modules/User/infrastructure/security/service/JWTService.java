package com.example.autofetch.modules.User.infrastructure.security.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.autofetch.modules.User.infrastructure.security.config.JwtProperties;

@Service
public class JWTService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JwtProperties jwtProperties;

    public String generateAcessToken(String email) {
        Instant now = Instant.now();
        long expiry = now.plus(jwtProperties.getAccessTokenExpiration()).getEpochSecond() - now.getEpochSecond();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("autofetch-api")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiry))
            .subject(email)
            .claim("type", "access")
            .build();

        return jwtEncoder.encode(
            JwtEncoderParameters.from(claims))
            .getTokenValue();
    }

    public String generateRefreshToken(String email) {
        Instant now = Instant.now();
        long expiry = now.plus(jwtProperties.getRefreshTokenExpiration()).getEpochSecond() - now.getEpochSecond();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("autofetch-api")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiry))
            .subject(email)
            .claim("type", "refresh")
            .build();

        return jwtEncoder.encode(
            JwtEncoderParameters.from(claims))
            .getTokenValue();
    }

    public String getSubject(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public String getTokenType(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaimAsString("type");
    }

    public long getExpirationEpochSeconds(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        if (jwt.getExpiresAt() == null) {
            throw new IllegalStateException("Token não possui campo 'exp'");
        }

        return jwt.getExpiresAt().getEpochSecond();
    }
}
