package com.ma_sante_assurance.carte.dto;

import com.ma_sante_assurance.common.enums.CarteStatus;

import java.time.LocalDate;

public record CarteResponseDTO(
        String id,
        String souscriptionId,
        String numeroCarte,
        LocalDate dateEmission,
        LocalDate dateExpiration,
        String qrCode,
        CarteStatus statut
) {
}
