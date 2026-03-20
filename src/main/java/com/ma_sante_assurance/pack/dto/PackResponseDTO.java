package com.ma_sante_assurance.pack.dto;

import java.math.BigDecimal;

public record PackResponseDTO(
        String id,
        String code,
        String nom,
        String description,
        BigDecimal prix,
        Integer duree,
        Boolean actif
) {
}
