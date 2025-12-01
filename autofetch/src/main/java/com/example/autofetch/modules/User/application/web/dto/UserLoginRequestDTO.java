package com.example.autofetch.modules.User.application.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDTO {

    @NotBlank(message="Email cannot be null")
    @Email(message="Invalid email format")
    private String email;

    @NotBlank(message="Password cannot be null")
    private String password;
}