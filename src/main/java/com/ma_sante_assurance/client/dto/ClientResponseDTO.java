package com.ma_sante_assurance.client.dto;

import com.ma_sante_assurance.common.enums.GeneralStatus;

import java.time.Instant;
import java.time.LocalDate;

public record ClientResponseDTO(
        String id,
        String userId,
        String numeroAssurance,
        String prenom,
        String nom,
        LocalDate dateNaissance,
        String telephone,
        String adresse,
        String numeroCni,
        String photoUrl,
        String typeAssurance,
        GeneralStatus statut,
        String createdByAgentId,
        Instant createdAt
) {

    public record PageResult(
            java.util.List<ClientResponseDTO> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
    }
}
