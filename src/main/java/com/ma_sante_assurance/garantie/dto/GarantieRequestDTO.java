package com.ma_sante_assurance.garantie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record GarantieRequestDTO(
        @NotBlank String libelle,
        String description,
        @NotNull BigDecimal plafond
) {
}
