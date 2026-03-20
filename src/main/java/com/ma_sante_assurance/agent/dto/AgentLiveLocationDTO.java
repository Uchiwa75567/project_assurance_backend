package com.ma_sante_assurance.agent.dto;

import java.time.Instant;

public record AgentLiveLocationDTO(
        String agentId,
        String matricule,
        String prenom,
        String nom,
        String telephone,
        String statut,
        Double latitude,
        Double longitude,
        Double speedKmh,
        Boolean moving,
        Instant updatedAt
) {
}
