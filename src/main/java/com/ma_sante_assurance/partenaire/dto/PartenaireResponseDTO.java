package com.ma_sante_assurance.partenaire.dto;

public record PartenaireResponseDTO(
        String id,
        String userId,
        String nom,
        String type,
        String adresse,
        String telephone,
        Double latitude,
        Double longitude,
        Double distanceKm,
        Boolean actif
) {
}
