package com.ma_sante_assurance.garantie.dto;

import java.math.BigDecimal;

public record GarantieResponseDTO(
        String id,
        String libelle,
        String description,
        BigDecimal plafond
) {
}
