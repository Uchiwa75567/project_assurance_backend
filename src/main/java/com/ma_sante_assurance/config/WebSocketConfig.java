package com.ma_sante_assurance.config;

import com.ma_sante_assurance.agent.ws.AgentLocationWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final AgentLocationWebSocketHandler handler;
    private final String allowedOrigins;

    public WebSocketConfig(AgentLocationWebSocketHandler handler,
                           @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.handler = handler;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/agent-locations")
                .setAllowedOrigins(allowedOrigins.split(","));
    }
}
