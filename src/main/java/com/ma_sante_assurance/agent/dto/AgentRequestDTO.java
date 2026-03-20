package com.ma_sante_assurance.agent.dto;

import jakarta.validation.constraints.NotBlank;

public record AgentRequestDTO(
        @NotBlank String id,
        @NotBlank String matricule,
        @NotBlank String prenom,
        @NotBlank String nom,
        @NotBlank String telephone,
        @NotBlank String statut
) {
}
