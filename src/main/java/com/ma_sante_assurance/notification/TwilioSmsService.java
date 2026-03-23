package com.ma_sante_assurance.notification;

import com.ma_sante_assurance.config.TwilioConfig;
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
            Message.creator(
                new PhoneNumber(phone),
                new PhoneNumber(twilioConfig.getFromPhone()),
                "Votre code OTP M&A Sante Assurance : " + code + " (valable 5 min)"
            ).create();

            log.info("OTP SMS envoyé à {}", phone);
        } catch (Exception e) {
            log.error("Erreur envoi SMS à {}: {}", phone, e.getMessage());
            throw new RuntimeException("Échec envoi SMS", e);
        }
    }

    @Override
    public void sendCardNumber(String phone, String fullName, String numeroAssurance) {
        try {
            Message.creator(
                new PhoneNumber(phone),
                new PhoneNumber(twilioConfig.getFromPhone()),
                "Bonjour " + fullName + ", votre numero de carte d'assurance est : " + numeroAssurance
            ).create();

            log.info("Numéro de carte SMS envoyé à {}", phone);
        } catch (Exception e) {
            log.error("Erreur envoi numéro de carte SMS à {}: {}", phone, e.getMessage());
            throw new RuntimeException("Échec envoi numéro de carte SMS", e);
        }
    }
}
