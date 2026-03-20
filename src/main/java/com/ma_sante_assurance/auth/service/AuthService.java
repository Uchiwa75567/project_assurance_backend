package com.ma_sante_assurance.auth.service;

import com.ma_sante_assurance.auth.dto.AuthRequestDTO;
import com.ma_sante_assurance.common.messages.AuthMessages;
import com.ma_sante_assurance.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthSessionService authSessionService;
    private final RegistrationService registrationService;

    public AuthService(PasswordEncoder passwordEncoder,
                       AuthSessionService authSessionService,
                       RegistrationService registrationService) {
        this.passwordEncoder = passwordEncoder;
        this.authSessionService = authSessionService;
        this.registrationService = registrationService;
    }

    @Transactional
    public IssuedSession register(AuthRequestDTO.RegisterRequest request) {
        RegistrationResult result = registrationService.register(request);
        return authSessionService.createSession(result.user());
    }

    @Transactional
    public IssuedSession login(AuthRequestDTO.LoginRequest request) {
        User user = registrationService.findUserByIdentifier(request.identifier());

        if (!Boolean.TRUE.equals(user.getActif())) {
            throw new IllegalArgumentException(AuthMessages.ACCOUNT_INACTIVE);
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException(AuthMessages.INVALID_CREDENTIALS);
        }

        return authSessionService.createSession(user);
    }

    @Transactional
    public IssuedSession refresh(String refreshToken) {
        return authSessionService.refresh(refreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        authSessionService.logout(refreshToken);
    }

    @Transactional(readOnly = true)
    public com.ma_sante_assurance.auth.dto.AuthResponseDTO.SessionResponse me(String accessToken) {
        return authSessionService.me(accessToken);
    }

}
