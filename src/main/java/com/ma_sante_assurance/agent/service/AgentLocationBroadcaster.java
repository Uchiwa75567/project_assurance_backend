package com.ma_sante_assurance.agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma_sante_assurance.agent.dto.AgentLiveLocationDTO;
import com.ma_sante_assurance.agent.ws.AgentLocationWebSocketHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentLocationBroadcaster {

    private final AgentLocationWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public AgentLocationBroadcaster(AgentLocationWebSocketHandler webSocketHandler, ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    public void broadcastSnapshot(List<AgentLiveLocationDTO> snapshot) {
        try {
            String json = objectMapper.writeValueAsString(snapshot);
            webSocketHandler.broadcast(json);
        } catch (JsonProcessingException ignored) {
            // no-op for MVP
        }
    }
}
