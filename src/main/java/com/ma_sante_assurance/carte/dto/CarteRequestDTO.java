package com.ma_sante_assurance.carte.dto;

import com.ma_sante_assurance.common.enums.CarteStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CarteRequestDTO(
        @NotBlank String souscriptionId,
        String numeroCarte,
        LocalDate dateEmission,
        LocalDate dateExpiration,
        String qrCode,
        CarteStatus statut
) {
}
