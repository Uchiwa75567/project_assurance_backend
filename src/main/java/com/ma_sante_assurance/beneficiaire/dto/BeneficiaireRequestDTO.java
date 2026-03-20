package com.ma_sante_assurance.beneficiaire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BeneficiaireRequestDTO(
        @NotBlank String souscriptionId,
        @NotBlank String nom,
        @NotBlank String prenom,
        LocalDate dateNaissance,
        String lien,
        @NotNull Boolean isPrincipal
) {
}
