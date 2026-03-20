package com.ma_sante_assurance.packgarantie.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record PackGarantieRequestDTO(
        @NotBlank String packId,
        @NotBlank String garantieId,
        BigDecimal plafondSpecifique
) {
}
