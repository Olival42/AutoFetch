package com.example.autofetch.modules.User.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.autofetch.modules.User.domain.entity.User;

public interface IUserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}