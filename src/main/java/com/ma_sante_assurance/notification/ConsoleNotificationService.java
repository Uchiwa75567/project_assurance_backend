package com.ma_sante_assurance.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationService.class);

    @Override
    public void sendNumeroAssurance(String fullName, String code, String email, String telephone) {
        String destination = email != null && !email.isBlank()
                ? "email=" + email
                : (telephone != null && !telephone.isBlank() ? "telephone=" + telephone : "destinataire inconnu");
        log.info("[Notification] OTP pour {} -> {} ({})", fullName, code, destination);
    }

    @Override
    public void sendCarteAssurance(String fullName, String numeroAssurance, String email, String telephone) {
        String destination = email != null && !email.isBlank()
                ? "email=" + email
                : (telephone != null && !telephone.isBlank() ? "telephone=" + telephone : "destinataire inconnu");
        log.info("[Notification] Carte pour {} -> {} ({})", fullName, numeroAssurance, destination);
    }
}
