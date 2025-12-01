package com.example.autofetch.modules.User.application.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
	private String id;
	private String name;
	private String email;
	private String accessToken;
    private Long expiresAt;
	private String refreshToken;
}
