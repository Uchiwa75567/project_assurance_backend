package com.ma_sante_assurance.souscription.dto;

import com.ma_sante_assurance.common.enums.SouscriptionStatus;
import com.ma_sante_assurance.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class SouscriptionRequestDTO {

    public record Create(
            @NotBlank String clientId,
            String agentId,
            @NotBlank String packId,
            @NotNull(message = ValidationMessages.DATE_REQUIRED) LocalDate dateDebut,
            @NotNull(message = ValidationMessages.DATE_REQUIRED) LocalDate dateFin,
            LocalDate dateProchainPaiement,
            SouscriptionStatus statut
    ) {
    }

    public record Update(
            String clientId,
            String agentId,
            String packId,
            LocalDate dateDebut,
            LocalDate dateFin,
            LocalDate dateProchainPaiement,
            SouscriptionStatus statut
    ) {
    }
}
