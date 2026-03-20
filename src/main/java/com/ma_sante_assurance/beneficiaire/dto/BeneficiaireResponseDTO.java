package com.ma_sante_assurance.beneficiaire.dto;

import java.time.LocalDate;

public record BeneficiaireResponseDTO(
        String id,
        String souscriptionId,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String lien,
        Boolean isPrincipal
) {
}
