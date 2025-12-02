package com.example.autofetch.modules.User.application.web.controller;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.autofetch.modules.User.application.web.dto.UserAuthTokenDTO;
import com.example.autofetch.modules.User.application.web.dto.UserLoginRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserRegisterRequestDTO;
import com.example.autofetch.modules.User.application.web.dto.UserResponseDTO;
import com.example.autofetch.modules.User.domain.service.UserService;
import com.example.autofetch.modules.User.infrastructure.security.service.JWTService;
import com.example.autofetch.shared.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class UserController {

        @Autowired
        private UserService userService;

        @Autowired
        private JWTService jwtService;

        @PostMapping("/register")
        public ResponseEntity<ApiResponse<?>> registerUser(
                        @RequestBody @Valid UserRegisterRequestDTO userRegisterRequestDTO, UriComponentsBuilder uri) {

                UserResponseDTO userResponseDTO = userService.registerUser(userRegisterRequestDTO);

                UserAuthTokenDTO tokens = userService.generateTokens(userResponseDTO.getEmail());

                ResponseCookie cookie = createRefreshCookie(
                                tokens.getRefreshToken(),
                                jwtService.getExpirationEpochSeconds(tokens.getRefreshToken())
                                                - Instant.now().getEpochSecond());

                URI url = uri.path("/users/{id}").buildAndExpand(userResponseDTO.getId()).toUri();

                var tokensMap = Map.of(
                                "accessToken", tokens.getAccessToken(),
                                "expiresAt", tokens.getExpiresAt());

                ApiResponse<?> response = ApiResponse.builder()
                                .success(true)
                                .data(Map.of(
                                                "user", userResponseDTO,
                                                "tokens", tokensMap))
                                .build();

                return ResponseEntity.created(url).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody @Valid UserLoginRequestDTO userLoginRequestDTO,
                        UriComponentsBuilder uri) {

                UserResponseDTO userResponseDTO = userService.loginUser(userLoginRequestDTO);

                UserAuthTokenDTO tokens = userService.generateTokens(userResponseDTO.getEmail());

                ResponseCookie cookie = createRefreshCookie(
                                tokens.getRefreshToken(),
                                jwtService.getExpirationEpochSeconds(tokens.getRefreshToken())
                                                - Instant.now().getEpochSecond());

                var tokensMap = Map.of(
                                "accessToken", tokens.getAccessToken(),
                                "expiresAt", tokens.getExpiresAt());

                ApiResponse<?> response = ApiResponse.builder()
                                .success(true)
                                .data(Map.of(
                                                "user", userResponseDTO,
                                                "tokens", tokensMap))
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(response);
        }

        @PostMapping("/logout")
        public ResponseEntity<ApiResponse<?>> logoutUser(@RequestHeader("Authorization") String header,
                        @CookieValue(value = "refreshToken", required = false) String refreshToken) {

                String tokenAccessToken = header.substring(7);
                userService.logoutUser(tokenAccessToken, refreshToken);

                ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(0)
                                .sameSite("Strict")
                                .build();

                ApiResponse<?> response = ApiResponse.builder()
                                .success(true)
                                .data("Logged out successfully")
                                .error(null)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                                .body(response);
        }

        private ResponseCookie createRefreshCookie(String refreshToken, long expirationSeconds) {
                return ResponseCookie.from("refreshToken", refreshToken)
                                .httpOnly(true)
                                .secure(true) // Recomendo true para produção
                                .path("/")
                                .maxAge(expirationSeconds)
                                .sameSite("Strict")
                                .build();
        }

        @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<?>> refreshTokens(
                        @CookieValue(value = "refreshToken", required = false) String refreshToken) {

                UserAuthTokenDTO tokens = userService.refreshTokens(refreshToken);

                ResponseCookie cookie = createRefreshCookie(
                                tokens.getRefreshToken(),
                                jwtService.getExpirationEpochSeconds(tokens.getRefreshToken())
                                                - Instant.now().getEpochSecond());

                var tokensMap = Map.of(
                                "accessToken", tokens.getAccessToken(),
                                "expiresAt", tokens.getExpiresAt());

                ApiResponse<?> response = ApiResponse.builder()
                                .success(true)
                                .data(Map.of("tokens", tokensMap))
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
        }
}
