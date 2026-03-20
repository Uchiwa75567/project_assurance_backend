package com.ma_sante_assurance.souscription.dto;

import com.ma_sante_assurance.common.enums.SouscriptionStatus;

import java.time.Instant;
import java.time.LocalDate;

public record SouscriptionResponseDTO(
        String id,
        String clientId,
        String agentId,
        String packId,
        LocalDate dateDebut,
        LocalDate dateFin,
        LocalDate dateProchainPaiement,
        SouscriptionStatus statut,
        Instant createdAt
) {
}
