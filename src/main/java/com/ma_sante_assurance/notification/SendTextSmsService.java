package com.ma_sante_assurance.notification;

import com.ma_sante_assurance.config.SendTextConfig;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;

@Slf4j
@Service("sendTextSms")
public class SendTextSmsService implements SmsService {

    private static final String SMS_PATH = "/send-sms";

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
        log.info("OTP SMS envoyé à {} via SendText", phone);
    }

    @Override
    public void sendCardNumber(String phone, String fullName, String numeroAssurance) {
        sendMessage(
                normalizePhone(phone),
                "Bonjour " + fullName + ", votre numero de carte d'assurance est : " + numeroAssurance
        );
        log.info("Numéro de carte SMS envoyé à {} via SendText", phone);
    }

    private void sendMessage(String toPhone, String message) {
        if (sendTextConfig.getApiKey() == null || sendTextConfig.getApiKey().isBlank()) {
            throw new IllegalStateException(
                    "SendText n'est pas configure. Verifie SENDTEXT_API_KEY dans deploy.env."
            );
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("to", toPhone);
        payload.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        applyAuthentication(headers, payload);

        String endpoint = normalizeBaseUrl(sendTextConfig.getBaseUrl()) + SMS_PATH;
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("Réponse SendText inattendue: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            log.error("Erreur SendText pour {}: status={} body={}", toPhone, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Échec envoi SMS", e);
        } catch (RestClientException e) {
            log.error("Erreur réseau SendText pour {}: {}", toPhone, e.getMessage());
            throw new RuntimeException("Échec envoi SMS", e);
        }
    }

    private void applyAuthentication(HttpHeaders headers, Map<String, Object> payload) {
        String apiKey = sendTextConfig.getApiKey() == null ? "" : sendTextConfig.getApiKey().trim();
        String apiSecret = sendTextConfig.getApiSecret() == null ? "" : sendTextConfig.getApiSecret().trim();
        String authMode = normalizeAuthMode(sendTextConfig.getAuthMode());

        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "SendText n'est pas configure. Verifie SENDTEXT_API_KEY dans deploy.env."
            );
        }

        if ("body".equals(authMode)) {
            payload.put("api_key", apiKey);
            return;
        }

        if ("bearer".equals(authMode)) {
            headers.setBearerAuth(apiKey);
            return;
        }

        if ("basic".equals(authMode)) {
            if (apiSecret.isBlank()) {
                throw new IllegalStateException(
                        "SENDTEXT_AUTH_MODE=basic requiert SENDTEXT_API_SECRET dans deploy.env."
                );
            }
            headers.setBasicAuth(apiKey, apiSecret);
            return;
        }

        throw new IllegalStateException(
                "Mode d'authentification SendText inconnu: " + authMode + ". Valeurs attendues: basic ou bearer."
        );
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "https://api.sendtext.sn";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl.trim();
    }

    private String normalizeAuthMode(String authMode) {
        if (authMode == null || authMode.isBlank()) {
            return "basic";
        }
        return authMode.trim().toLowerCase(Locale.ROOT);
    }

    private RestTemplate buildRestTemplate(int connectTimeoutMs, int readTimeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        return new RestTemplate(factory);
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
            normalized = normalized.substring(1);
        }

        if (!normalized.startsWith("221")) {
            // Default to Senegal international format for local numbers like 771234567.
            if (normalized.matches("\\d{9}")) {
                normalized = "221" + normalized;
            }
        }

        return normalized;
    }
}
