package com.ma_sante_assurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MaSanteAssuranceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaSanteAssuranceApplication.class, args);
    }
}
