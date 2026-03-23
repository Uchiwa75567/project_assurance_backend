package com.ma_sante_assurance.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ma_sante_assurance.config.MailjetConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service("mailjetEmail")
public class MailjetEmailService implements EmailService {

    private final MailjetConfig mailjetConfig;
    private final RestTemplate restTemplate;

    public MailjetEmailService(MailjetConfig mailjetConfig,
                               @Qualifier("mailjetRestTemplate") RestTemplate restTemplate) {
        this.mailjetConfig = mailjetConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendOtp(String email, String code) {
        sendEmail(
                email,
                "Votre code OTP",
                """
                <h2>Félicitations !</h2>
                <p>Votre code OTP : <strong>%s</strong></p>
                <p>Conservez-le précieusement.</p>
                """.formatted(code),
                "Votre code OTP M&A Sante Assurance : " + code + " (valable 5 min)"
        );
    }

    @Override
    public void sendCardNumber(String email, String fullName, String numeroAssurance) {
        sendEmail(
                email,
                "Votre numéro de carte d'assurance",
                """
                <h2>Bonjour %s</h2>
                <p>Votre numéro de carte d'assurance est :</p>
                <p><strong>%s</strong></p>
                <p>Gardez ce numéro précieusement.</p>
                """.formatted(fullName, numeroAssurance),
                "Bonjour %s, votre numéro de carte d'assurance est : %s".formatted(fullName, numeroAssurance)
        );
    }

    private void sendEmail(String recipientEmail, String subject, String htmlContent, String textContent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(mailjetConfig.getApiKey(), mailjetConfig.getApiSecret());
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            var request = new MailjetMessageRequest(
                    List.of(new Message(
                            new Sender(mailjetConfig.getSenderEmail(), mailjetConfig.getSenderName()),
                            List.of(new Recipient(recipientEmail)),
                            subject,
                            textContent,
                            htmlContent
                    ))
            );

            HttpEntity<MailjetMessageRequest> entity = new HttpEntity<>(request, headers);
            var response = restTemplate.postForEntity(mailjetConfig.getApiUrl() + "/send", entity, String.class);

            int status = response.getStatusCode().value();
            if (status < 200 || status >= 300) {
                throw new RuntimeException("Mailjet status=" + status + ", body=" + response.getBody());
            }

            log.info("Email envoyé à {} via Mailjet", recipientEmail);
        } catch (Exception e) {
            log.error("Erreur envoi email à {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Échec envoi email", e);
        }
    }

    public record MailjetMessageRequest(@JsonProperty("Messages") List<Message> messages) {}

    public record Message(
            @JsonProperty("From") Sender from,
            @JsonProperty("To") List<Recipient> to,
            @JsonProperty("Subject") String subject,
            @JsonProperty("TextPart") String textPart,
            @JsonProperty("HTMLPart") String htmlPart
    ) {}

    public record Sender(@JsonProperty("Email") String email, @JsonProperty("Name") String name) {}

    public record Recipient(@JsonProperty("Email") String email) {}
}
