package com.ma_sante_assurance.config;

import com.twilio.Twilio;
import com.twilio.http.NetworkHttpClient;
import com.twilio.http.TwilioRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class TwilioConfig {

    @Value("${app.twilio.account-sid:}")
    private String accountSid;

    @Value("${app.twilio.auth-token:}")
    private String authToken;

    @Value("${app.twilio.from-phone:}")
    private String fromPhone;

    @Value("${app.twilio.connect-timeout-ms:10000}")
    private int connectTimeoutMs;

    @Value("${app.twilio.socket-timeout-ms:10000}")
    private int socketTimeoutMs;

    @Value("${app.twilio.request-timeout-ms:10000}")
    private int requestTimeoutMs;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isBlank() && authToken != null && !authToken.isBlank()) {
            Twilio.init(accountSid, authToken);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeoutMs)
                    .setConnectionRequestTimeout(requestTimeoutMs)
                    .setSocketTimeout(socketTimeoutMs)
                    .build();
            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoTimeout(socketTimeoutMs)
                    .build();
            Twilio.setRestClient(new TwilioRestClient.Builder(accountSid, authToken)
                    .httpClient(new NetworkHttpClient(requestConfig, socketConfig))
                    .build());
            log.info("Twilio initialisé avec timeouts connect={}ms socket={}ms request={}ms",
                    connectTimeoutMs, socketTimeoutMs, requestTimeoutMs);
        }
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getFromPhone() {
        return fromPhone;
    }
}
