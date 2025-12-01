package com.example.autofetch.modules.User.application.web.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDTO {

    @Length(min = 5, max = 100, message = "Username must be between 5 and 100 characters")
    @NotBlank(message="Username cannot be null")
    private String userName;

    @NotBlank(message="Email cannot be null")
    @Email(message="Invalid email format")
    private String email;

    @Length(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message="Password cannot be null")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;
}