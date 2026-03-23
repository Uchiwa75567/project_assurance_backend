package com.ma_sante_assurance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MailjetConfig {

    @Value("${app.mailjet.api-key}")
    private String apiKey;

    @Value("${app.mailjet.api-secret}")
    private String apiSecret;

    @Value("${app.mailjet.sender-email:no-reply@ma-sante-assurance.sn}")
    private String senderEmail;

    @Value("${app.mailjet.sender-name:M&A Sante Assurance}")
    private String senderName;

    @Value("${app.mailjet.api-url:https://api.mailjet.com/v3.1}")
    private String apiUrl;

    @Bean("mailjetRestTemplate")
    public RestTemplate mailjetRestTemplate() {
        return new RestTemplate();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
