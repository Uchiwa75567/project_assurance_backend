package com.ma_sante_assurance.agent.dto;

public record AgentResponseDTO(
        String id,
        String matricule,
        String prenom,
        String nom,
        String telephone,
        String statut
) {
}
