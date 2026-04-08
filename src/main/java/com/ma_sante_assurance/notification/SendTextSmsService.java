package com.ma_sante_assurance.notification;

import com.ma_sante_assurance.config.SendTextConfig;
import com.ma_sante_assurance.notification.dto.SmsRequest;
import com.ma_sante_assurance.notification.dto.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service("sendTextSms")
public class SendTextSmsService implements SmsService {

    private static final String SMS_PATH = "/sms";

    private final SendTextConfig sendTextConfig;
    private final RestTemplate restTemplate;

    public SendTextSmsService(SendTextConfig sendTextConfig) {
        this.sendTextConfig = sendTextConfig;
        this.restTemplate = buildRestTemplate(
                sendTextConfig.getConnectTimeoutMs(),
                sendTextConfig.getReadTimeoutMs()
        );
    }

    @Override
    public void sendOtp(String phone, String code) {
        sendMessage(normalizePhone(phone), "Votre code OTP M&A Sante Assurance : " + code + " (valable 5 min)");
        log.info("OTP SMS envoyé à {} via Sendtext", phone);
    }

    @Override
    public void sendCardNumber(String phone, String fullName, String numeroAssurance) {
        sendMessage(
                normalizePhone(phone),
                "Bonjour " + fullName + ", votre numero de carte d'assurance est : " + numeroAssurance
        );
        log.info("Numéro de carte SMS envoyé à {} via Sendtext", phone);
    }

    private SmsResponse sendMessage(String toPhone, String message) {
        if (sendTextConfig.getKey() == null || sendTextConfig.getKey().isBlank()
                || sendTextConfig.getSecret() == null || sendTextConfig.getSecret().isBlank()
                || sendTextConfig.getSenderName() == null || sendTextConfig.getSenderName().isBlank()) {
            throw new IllegalStateException(
                    "Sendtext n'est pas configure. Verifie SENDTEXT_API_KEY, SENDTEXT_API_SECRET et SENDTEXT_FROM/SENDTEXT_SENDER_NAME dans deploy.env."
            );
        }

        SmsRequest payload = SmsRequest.builder()
                .senderName(sendTextConfig.getSenderName().trim())
                .smsType("normal")
                .phone(toPhone)
                .text(message)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("SNT-API-KEY", sendTextConfig.getKey().trim());
        headers.set("SNT-API-SECRET", sendTextConfig.getSecret().trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String endpoint = normalizeBaseUrl(sendTextConfig.getBaseUrl()) + SMS_PATH;
        HttpEntity<SmsRequest> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<SmsResponse> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, SmsResponse.class);
            SmsResponse body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("Réponse Sendtext vide");
            }

            if (!isSuccessfulStatus(body.getStatusId())) {
                throw new IllegalStateException(
                        "Statut Sendtext inattendu: " + body.getStatusId() + " - " + body.getStatusDescription()
                );
            }

            log.info("Sendtext SMS accepté pour {} avec statusId={}", toPhone, body.getStatusId());
            return body;
        } catch (HttpStatusCodeException e) {
            log.error("Erreur Sendtext pour {}: status={} body={}", toPhone, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Échec envoi SMS", e);
        } catch (RestClientException e) {
            log.error("Erreur réseau Sendtext pour {}: {}", toPhone, e.getMessage());
            throw new RuntimeException("Échec envoi SMS", e);
        }
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "https://api.sendtext.sn/v1";
        }
        String normalized = baseUrl.trim();
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.endsWith("/v1") ? normalized : normalized + "/v1";
    }

    private RestTemplate buildRestTemplate(int connectTimeoutMs, int readTimeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        return new RestTemplate(factory);
    }

    private boolean isSuccessfulStatus(Integer statusId) {
        return statusId != null && (statusId == 1 || statusId == 3);
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }

        String normalized = phone.trim().replace(" ", "").replace("-", "");

        if (normalized.startsWith("00")) {
            normalized = normalized.substring(2);
        }

        if (normalized.startsWith("+")) {
            return normalized.substring(1);
        }

        if (normalized.matches("\\d{9}")) {
            return "221" + normalized;
        }

        if (normalized.matches("221\\d{9}")) {
            return normalized;
        }

        return normalized;
    }
}
