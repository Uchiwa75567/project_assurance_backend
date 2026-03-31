package com.ma_sante_assurance.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!local")
@Primary
@Service
@Slf4j
public class RealNotificationService implements NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;

    public RealNotificationService(@Qualifier("mailjetEmail") EmailService emailService, SmsService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @Override
    public void sendNumeroAssurance(String fullName, String code, String email, String telephone) {
        boolean emailSent = false;
        boolean smsSent = false;
        RuntimeException lastError = null;

        // Email
        if (email != null && !email.isBlank()) {
            try {
                emailService.sendOtp(email, code);
                log.info("✅ Email OTP envoyé à {}", email);
                emailSent = true;
            } catch (Exception e) {
                log.error("❌ Erreur email à {} : {}", email, e.getMessage());
                lastError = new RuntimeException("Échec envoi email OTP", e);
            }
        }
        
        // SMS
        if (telephone != null && !telephone.isBlank()) {
            try {
                smsService.sendOtp(telephone, code);
                log.info("✅ SMS OTP envoyé à {}", telephone);
                smsSent = true;
            } catch (Exception e) {
                log.error("❌ Erreur SMS à {} : {}", telephone, e.getMessage());
                lastError = new RuntimeException("Échec envoi SMS OTP", e);
            }
        }

        if (!emailSent && !smsSent && lastError != null) {
            throw lastError;
        }
    }

    @Override
    public void sendCarteAssurance(String fullName, String numeroAssurance, String email, String telephone) {
        boolean emailSent = false;
        boolean smsSent = false;
        RuntimeException lastError = null;

        if (email != null && !email.isBlank()) {
            try {
                emailService.sendCardNumber(email, fullName, numeroAssurance);
                log.info("✅ Email carte envoyé à {}", email);
                emailSent = true;
            } catch (Exception e) {
                log.error("❌ Erreur email carte à {} : {}", email, e.getMessage());
                lastError = new RuntimeException("Échec envoi email carte", e);
            }
        }

        if (telephone != null && !telephone.isBlank()) {
            try {
                smsService.sendCardNumber(telephone, fullName, numeroAssurance);
                log.info("✅ SMS carte envoyé à {}", telephone);
                smsSent = true;
            } catch (Exception e) {
                log.error("❌ Erreur SMS carte à {} : {}", telephone, e.getMessage());
                lastError = new RuntimeException("Échec envoi SMS carte", e);
            }
        }

        if (!emailSent && !smsSent && lastError != null) {
            throw lastError;
        }
    }
}
