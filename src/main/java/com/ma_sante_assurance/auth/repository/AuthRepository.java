package com.ma_sante_assurance.auth.repository;

import com.ma_sante_assurance.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, String> {
    Optional<Auth> findByRefreshTokenHashAndActiveTrue(String refreshTokenHash);
}
