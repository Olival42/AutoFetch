package com.example.autofetch.modules.User.domain.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reset_password_tokens")
@Data
public class ResetPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;
}
