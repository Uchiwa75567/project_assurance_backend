package com.ma_sante_assurance.pack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PackRequestDTO(
        @NotBlank String code,
        @NotBlank String nom,
        String description,
        @NotNull BigDecimal prix,
        @NotNull Integer duree,
        Boolean actif
) {
}
