package com.ma_sante_assurance.notification;

import com.ma_sante_assurance.config.TwilioConfig;
import com.twilio.exception.ApiConnectionException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("twilioSms")
@RequiredArgsConstructor
public class TwilioSmsService implements SmsService {

    private final TwilioConfig twilioConfig;

    @Override
    public void sendOtp(String phone, String code) {
        try {
            if (twilioConfig.getAccountSid() == null || twilioConfig.getAccountSid().isBlank()
                    || twilioConfig.getAuthToken() == null || twilioConfig.getAuthToken().isBlank()
                    || twilioConfig.getFromPhone() == null || twilioConfig.getFromPhone().isBlank()) {
                throw new IllegalStateException(
                        "Twilio n'est pas configure. Verifie TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN et TWILIO_FROM_PHONE."
                );
            }

            sendWithRetry(
                    normalizePhone(phone),
                    normalizePhone(twilioConfig.getFromPhone()),
                    "Votre code OTP M&A Sante Assurance : " + code + " (valable 5 min)"
            );

            log.info("OTP SMS envoyé à {} (normalisé: {})", phone, normalizePhone(phone));
        } catch (Exception e) {
            log.error("Erreur envoi SMS à {}: {}", phone, e.getMessage());
            throw new RuntimeException("Échec envoi SMS", e);
        }
    }

    @Override
    public void sendCardNumber(String phone, String fullName, String numeroAssurance) {
        try {
            if (twilioConfig.getAccountSid() == null || twilioConfig.getAccountSid().isBlank()
                    || twilioConfig.getAuthToken() == null || twilioConfig.getAuthToken().isBlank()
                    || twilioConfig.getFromPhone() == null || twilioConfig.getFromPhone().isBlank()) {
                throw new IllegalStateException(
                        "Twilio n'est pas configure. Verifie TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN et TWILIO_FROM_PHONE."
                );
            }

            String normalizedPhone = normalizePhone(phone);
            sendWithRetry(
                    normalizedPhone,
                    normalizePhone(twilioConfig.getFromPhone()),
                    "Bonjour " + fullName + ", votre numero de carte d'assurance est : " + numeroAssurance
            );

            log.info("Numéro de carte SMS envoyé à {} (normalisé: {})", phone, normalizedPhone);
        } catch (Exception e) {
            log.error("Erreur envoi numéro de carte SMS à {}: {}", phone, e.getMessage());
            throw new RuntimeException("Échec envoi numéro de carte SMS", e);
        }
    }

    private void sendWithRetry(String toPhone, String fromPhone, String body) {
        int attempts = 0;
        RuntimeException lastError = null;

        while (attempts < 3) {
            attempts++;
            try {
                Message.creator(
                        new PhoneNumber(toPhone),
                        new PhoneNumber(fromPhone),
                        body
                ).create();
                return;
            } catch (ApiConnectionException e) {
                lastError = e;
                log.warn("Tentative SMS Twilio {} echouee pour {}: {}", attempts, toPhone, e.getMessage());
                if (attempts < 3) {
                    try {
                        Thread.sleep(1000L * attempts);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Échec envoi SMS", interruptedException);
                    }
                }
            }
        }

        if (lastError != null) {
            throw lastError;
        }
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }

        String normalized = phone.trim().replace(" ", "").replace("-", "");

        if (normalized.startsWith("00")) {
            normalized = "+" + normalized.substring(2);
        }

        if (!normalized.startsWith("+")) {
            // Default to Senegal international format for local numbers like 771234567.
            if (normalized.matches("\\d{9}")) {
                normalized = "+221" + normalized;
            }
        }

        return normalized;
    }
}
