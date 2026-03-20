package com.ma_sante_assurance.partenaire.dto;

import com.ma_sante_assurance.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

    public record PartenaireRequestDTO(
            String userId,
            @NotBlank String nom,
            @NotBlank String type,
            String adresse,
            @Pattern(regexp = "^$|[+0-9][0-9\\s-]{6,20}$", message = ValidationMessages.PHONE_INVALID) String telephone,
            @NotNull Double latitude,
            @NotNull Double longitude,
            Boolean actif
    ) {
}
