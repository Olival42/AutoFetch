package com.example.autofetch.modules.User.domain.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    /* @Column(nullable = true)
    private String googleAccessToken;

    @Column(nullable = true)
    private String googleRefreshToken; */

    private Instant createAt;

    public void encoderPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    public boolean matchPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, this.password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Se você tiver roles (ex: ADMIN, USER), pode mapeá-las aqui.
        // Por enquanto, retornaremos uma lista vazia.
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email; // O Spring Security usa este método para autenticação
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}