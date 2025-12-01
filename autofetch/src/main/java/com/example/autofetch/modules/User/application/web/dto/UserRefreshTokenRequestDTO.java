package com.example.autofetch.modules.User.application.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRefreshTokenRequestDTO {

    @NotBlank(message="Refresh token is required")
    private String refreshToken;
}
