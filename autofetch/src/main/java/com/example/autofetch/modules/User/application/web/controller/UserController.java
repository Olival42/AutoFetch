package com.example.autofetch.modules.User.application.web.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.autofetch.modules.User.application.web.dto.UserLoginRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserRefreshTokenRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserRegisterRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserResponseDTO;
import com.example.autofetch.modules.User.domain.service.UserService;
import com.example.autofetch.shared.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody @Valid UserRegisterRequestDTO userRegisterRequestDTO, UriComponentsBuilder uri) {
        
        UserResponseDTO userResponseDTO = userService.registerUser(userRegisterRequestDTO);
        
        URI url = uri.path("/users/{id}").buildAndExpand(userResponseDTO.getId()).toUri();

        ApiResponse<?> response = ApiResponse.builder()
            .success(true)
            .data(userResponseDTO)
            .error(null)
            .build();

        return ResponseEntity.created(url).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody @Valid UserLoginRequestDTO userLoginRequestDTO, UriComponentsBuilder uri) {
        
        UserResponseDTO userResponseDTO = userService.loginUser(userLoginRequestDTO);
        
        ApiResponse<?> response = ApiResponse.builder()
            .success(true)
            .data(userResponseDTO)
            .error(null)
            .build();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logoutUser(@RequestHeader("Authorization") String header, @RequestBody @Valid UserRefreshTokenRequestDTO userRefreshTokenRequestDTO) {
        
        String tokenAcessToken = header.substring(7);
        userService.logoutUser(tokenAcessToken, userRefreshTokenRequestDTO.getRefreshToken());

        ApiResponse<?> response = ApiResponse.builder()
            .success(true)
            .data("Logged out successfully")
            .error(null)
            .build();

        return ResponseEntity.ok(response);
    }
}
