package com.example.autofetch.modules.User.domain.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.autofetch.infrastructure.exception.security.InvalidTokenTypeException;
import com.example.autofetch.infrastructure.exception.security.MissingTokenException;
import com.example.autofetch.infrastructure.exception.security.TokenExpiredException;
import com.example.autofetch.infrastructure.exception.security.TokenRevokedException;
import com.example.autofetch.modules.User.adapters.mapper.UserMapper;
import com.example.autofetch.modules.User.application.web.dto.UserAuthTokenDTO;
import com.example.autofetch.modules.User.application.web.dto.UserLoginRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserRegisterRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserResponseDTO;
import com.example.autofetch.modules.User.domain.entity.User;
import com.example.autofetch.modules.User.domain.repository.IUserRepository;
import com.example.autofetch.modules.User.infrastructure.security.service.JWTService;
import com.example.autofetch.modules.User.infrastructure.security.service.TokenBlacklistService;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Transactional
    public UserResponseDTO registerUser(UserRegisterRequestDTO userRegisterRequestDTO) {

        var userEntity = userMapper.toEntity(userRegisterRequestDTO);

        userEntity.encoderPassword(userRegisterRequestDTO.getPassword());

        var savedUser = userRepository.save(userEntity);

        return userMapper.toDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO loginUser(UserLoginRequestDTO userLoginRequestDTO) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(userLoginRequestDTO.getEmail(),
                userLoginRequestDTO.getPassword());

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        var userEntity = (User) auth.getPrincipal();

        return userMapper.toDTO(userEntity);
    }

    @Transactional
    public void logoutUser(String accessToken, String refreshToken) {

        if (refreshToken == null) {
            throw new MissingTokenException("Refresh token is missing.");
        }

        long expAccessToken = jwtService.getExpirationEpochSeconds(accessToken);
        long expRefreshToken = jwtService.getExpirationEpochSeconds(refreshToken);

        long now = Instant.now().getEpochSecond();

        long ttlAccessToken = expAccessToken - now;
        long ttlRefreshToken = expRefreshToken - now;

        if (ttlAccessToken > 0) {
            blacklistService.blacklistToken(accessToken, ttlAccessToken);
        }

        if (ttlRefreshToken > 0) {
            blacklistService.blacklistToken(refreshToken, ttlRefreshToken);
        }
    }

    @Transactional
    public UserAuthTokenDTO refreshTokens(String refreshToken) {

        if (refreshToken == null) {
            throw new MissingTokenException("Refresh token is missing.");
        }

        if (!jwtService.getTokenType(refreshToken).equals("refresh")) {
            throw new InvalidTokenTypeException("Invalid token type.");
        }

        if (blacklistService.isTokenBlacklisted(refreshToken)) {
            throw new TokenRevokedException("Refresh token is revoked.");
        }

        if (jwtService.getExpirationEpochSeconds(refreshToken) < Instant.now().getEpochSecond()) {
            throw new TokenExpiredException("Refresh token is expired.");
        }

        String email = jwtService.getEmailFromToken(refreshToken);

        long ttl = jwtService.getExpirationEpochSeconds(refreshToken) - Instant.now().getEpochSecond();
        if (ttl > 0) {
            blacklistService.blacklistToken(refreshToken, ttl);
        }

        return generateTokens(email);
    }

    @Transactional
    public UserAuthTokenDTO generateTokens(String email) {

        String accessToken = jwtService.generateAcessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);
        long expiresAt = jwtService.getExpirationEpochSeconds(accessToken);

        return UserAuthTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .build();
    }
}
