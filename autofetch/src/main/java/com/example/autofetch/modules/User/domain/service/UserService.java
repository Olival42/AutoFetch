package com.example.autofetch.modules.User.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.autofetch.modules.User.adapters.mapper.UserMapper;
import com.example.autofetch.modules.User.application.web.dto.UserRegisterRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserResponseDTO;
import com.example.autofetch.modules.User.domain.repository.IUserRepository;
import com.example.autofetch.modules.User.infrastructure.security.service.JWTService;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;
    
    @Autowired
    private JWTService jwtService;
    
    @Autowired
    private UserMapper userMapper;

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
}
