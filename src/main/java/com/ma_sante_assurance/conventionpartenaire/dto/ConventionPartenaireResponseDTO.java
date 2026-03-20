package com.ma_sante_assurance.conventionpartenaire.dto;

import java.math.BigDecimal;

public record ConventionPartenaireResponseDTO(
        String id,
        String packId,
        String packNom,
        String partenaireId,
        String partenaireNom,
        Boolean acceptee,
        BigDecimal tauxCouverture,
        BigDecimal plafond,
        Boolean actif
) {
}
