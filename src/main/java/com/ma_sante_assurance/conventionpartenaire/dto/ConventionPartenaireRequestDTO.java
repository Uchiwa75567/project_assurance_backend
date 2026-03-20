package com.ma_sante_assurance.conventionpartenaire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConventionPartenaireRequestDTO(
        @NotBlank String packId,
        @NotBlank String partenaireId,
        @NotNull Boolean acceptee,
        BigDecimal tauxCouverture,
        BigDecimal plafond,
        Boolean actif
) {
}
