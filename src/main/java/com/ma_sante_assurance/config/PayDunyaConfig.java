package com.ma_sante_assurance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PayDunyaConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
