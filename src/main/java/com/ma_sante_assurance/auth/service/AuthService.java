package com.ma_sante_assurance.auth.service;

import com.ma_sante_assurance.auth.dto.AuthRequestDTO;
import com.ma_sante_assurance.client.service.ClientService;
import com.ma_sante_assurance.common.enums.OtpType;
import com.ma_sante_assurance.common.messages.AuthMessages;
import com.ma_sante_assurance.common.util.EmailNormalizer;
import com.ma_sante_assurance.notification.NotificationService;
import com.ma_sante_assurance.otp.service.OtpService;
import com.ma_sante_assurance.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthSessionService authSessionService;
    private final RegistrationService registrationService;
    private final ClientService clientService;
    private final NotificationService notificationService;
    private final OtpService otpService;

    public AuthService(PasswordEncoder passwordEncoder,
                       AuthSessionService authSessionService,
                       RegistrationService registrationService,
                       ClientService clientService,
                       NotificationService notificationService,
                       OtpService otpService) {
        this.passwordEncoder = passwordEncoder;
        this.authSessionService = authSessionService;
        this.registrationService = registrationService;
        this.clientService = clientService;
        this.notificationService = notificationService;
        this.otpService = otpService;
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

    @Transactional
    public void verifyOtp(AuthRequestDTO.VerifyOtpRequest request) {
        String normalizedEmail = EmailNormalizer.normalize(request.email());
        if (normalizedEmail == null) {
            throw new IllegalArgumentException("Email requis");
        }

        User user = registrationService.findUserByIdentifier(normalizedEmail);
        boolean valid = otpService.validateOtp(user.getId(), OtpType.EMAIL, request.code().trim());
        if (!valid) {
            throw new IllegalArgumentException("Code OTP invalide ou expiré");
        }

        var client = clientService.findByUserId(user.getId());
        notificationService.sendCarteAssurance(
                user.getFullName(),
                client.numeroAssurance(),
                normalizedEmail,
                client.telephone()
        );
    }

    @Transactional
    public void resendOtp(AuthRequestDTO.ResendOtpRequest request) {
        String normalizedEmail = EmailNormalizer.normalize(request.email());
        if (normalizedEmail == null) {
            throw new IllegalArgumentException("Email requis");
        }

        User user = registrationService.findUserByIdentifier(normalizedEmail);
        var client = clientService.findByUserId(user.getId());
        String otpCode = otpService.generateOtp(user.getId(), OtpType.EMAIL);
        notificationService.sendNumeroAssurance(user.getFullName(), otpCode, normalizedEmail, client.telephone());
    }

}
