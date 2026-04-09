package com.ma_sante_assurance.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.sendtext.api")
@Data
@Slf4j
public class SendTextConfig {

    private String baseUrl = "https://api.sendtext.sn/v1";
    private String key;
    private String secret;
    private String senderName;
    private int connectTimeoutMs = 10000;
    private int readTimeoutMs = 10000;

    @PostConstruct
    public void init() {
        if (key != null && !key.isBlank()) {
            log.info(
                    "SendText initialisé avec baseUrl={} senderName={} timeout connect={}ms read={}ms",
                    baseUrl,
                    senderName,
                    connectTimeoutMs,
                    readTimeoutMs
            );
        }
    }
}
