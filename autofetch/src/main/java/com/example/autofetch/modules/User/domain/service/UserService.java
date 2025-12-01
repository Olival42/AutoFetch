package com.example.autofetch.modules.User.domain.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.autofetch.modules.User.adapters.mapper.UserMapper;
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

        String accessToken = jwtService.generateAcessToken(savedUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());
        
        UserResponseDTO userResponseDTO = userMapper.toDTO(savedUser);
        userResponseDTO.setAccessToken(accessToken);
        userResponseDTO.setRefreshToken(refreshToken);
        userResponseDTO.setExpiresAt(jwtService.getExpirationEpochSeconds(accessToken));

        return userResponseDTO; 
    }


    @Transactional
    public UserResponseDTO loginUser(UserLoginRequestDTO userLoginRequestDTO) {
        
        var usernamePassword = new UsernamePasswordAuthenticationToken(userLoginRequestDTO.getEmail(), userLoginRequestDTO.getPassword());

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        var userEntity = (User) auth.getPrincipal();
        
        String accessToken = jwtService.generateAcessToken(userEntity.getEmail());
        String refreshToken = jwtService.generateRefreshToken(userEntity.getEmail());
        
        UserResponseDTO userResponseDTO = userMapper.toDTO(userEntity);
        userResponseDTO.setAccessToken(accessToken);
        userResponseDTO.setRefreshToken(refreshToken);
        userResponseDTO.setExpiresAt(jwtService.getExpirationEpochSeconds(accessToken));

        return userResponseDTO; 
    }

    @Transactional
    public void logoutUser(String acessToken, String refreshToken) {

        long expAcessToken = jwtService.getExpirationEpochSeconds(acessToken);
        long expRefreshToken = jwtService.getExpirationEpochSeconds(refreshToken);

        long now = Instant.now().getEpochSecond();

        long ttlAcessToken = expAcessToken - now;
        long ttlRefreshToken = expRefreshToken - now;

        if (ttlAcessToken > 0) {
            blacklistService.blacklistToken(acessToken, ttlAcessToken);
        }
        
        if (ttlRefreshToken > 0) {
            blacklistService.blacklistToken(refreshToken, ttlRefreshToken);
        }
    }
}
