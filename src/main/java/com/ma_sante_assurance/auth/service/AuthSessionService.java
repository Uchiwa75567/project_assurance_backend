package com.ma_sante_assurance.auth.service;

import com.ma_sante_assurance.auth.dto.AuthResponseDTO;
import com.ma_sante_assurance.auth.entity.Auth;
import com.ma_sante_assurance.auth.repository.AuthRepository;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.security.jwt.JwtService;
import com.ma_sante_assurance.security.jwt.TokenHasher;
import com.ma_sante_assurance.user.entity.User;
import com.ma_sante_assurance.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthSessionService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final TokenHasher tokenHasher;

    public AuthSessionService(UserRepository userRepository,
                              AuthRepository authRepository,
                              JwtService jwtService,
                              TokenHasher tokenHasher) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
        this.jwtService = jwtService;
        this.tokenHasher = tokenHasher;
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO.SessionResponse me(String accessToken) {
        String userId = jwtService.parseClaims(accessToken).getSubject();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        return buildSession(user);
    }

    @Transactional
    public IssuedSession refresh(String refreshToken) {
        String refreshTokenHash = tokenHasher.hash(refreshToken);
        Auth session = authRepository.findByRefreshTokenHashAndActiveTrue(refreshTokenHash)
                .orElseThrow(() -> new EntityNotFoundException("Refresh token invalide"));

        if (session.getExpiresAt().isBefore(Instant.now())) {
            session.setActive(false);
            authRepository.save(session);
            throw new IllegalArgumentException("Refresh token expire");
        }

        Claims claims = jwtService.parseClaims(refreshToken);
        String type = claims.get("typ", String.class);
        if (!"refresh".equals(type)) {
            throw new IllegalArgumentException("Type de token invalide");
        }

        User user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        session.setActive(false);
        authRepository.save(session);

        return createSession(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        Auth session = authRepository.findByRefreshTokenHashAndActiveTrue(tokenHasher.hash(refreshToken))
                .orElseThrow(() -> new EntityNotFoundException("Session introuvable"));
        session.setActive(false);
        authRepository.save(session);
    }

    @Transactional
    public IssuedSession createSession(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        Auth session = Auth.builder()
                .id(IdGenerator.uuid())
                .userId(user.getId())
                .refreshTokenHash(tokenHasher.hash(refreshToken))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(jwtService.refreshTokenExpiresInSeconds(), ChronoUnit.SECONDS))
                .active(true)
                .build();

        authRepository.save(session);

        return new IssuedSession(accessToken, refreshToken, buildSession(user));
    }

    private AuthResponseDTO.SessionResponse buildSession(User user) {
        return new AuthResponseDTO.SessionResponse(
                jwtService.accessTokenExpiresInSeconds(),
                jwtService.refreshTokenExpiresInSeconds(),
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
