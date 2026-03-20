package com.ma_sante_assurance.packgarantie.dto;

import java.math.BigDecimal;

public record PackGarantieResponseDTO(
        String id,
        String packId,
        String garantieId,
        String garantieLibelle,
        String garantieDescription,
        BigDecimal garantiePlafond,
        BigDecimal plafondSpecifique
) {
}
