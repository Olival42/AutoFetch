package com.example.autofetch.modules.User.domain.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.autofetch.modules.User.domain.entity.ResetPasswordToken;

public interface IResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, String> {

    void deleteByEmail(String email);
    
    @Query("SELECT r FROM ResetPasswordToken r WHERE r.expiresAt > :now")
    List<ResetPasswordToken> findValidToken(@Param("now") Instant now);

}
