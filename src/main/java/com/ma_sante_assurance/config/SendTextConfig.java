package com.ma_sante_assurance.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SendTextConfig {

    @Value("${app.sendtext.api-key:}")
    private String apiKey;

    @Value("${app.sendtext.api-secret:}")
    private String apiSecret;

    @Value("${app.sendtext.from:}")
    private String from;

    @Value("${app.sendtext.base-url:https://api.sendtext.sn}")
    private String baseUrl;

    @Value("${app.sendtext.connect-timeout-ms:10000}")
    private int connectTimeoutMs;

    @Value("${app.sendtext.read-timeout-ms:10000}")
    private int readTimeoutMs;

    @PostConstruct
    public void init() {
        if (apiKey != null && !apiKey.isBlank()) {
            log.info("SendText initialisé avec timeouts connect={}ms read={}ms", connectTimeoutMs, readTimeoutMs);
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getFrom() {
        return from;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }
}
