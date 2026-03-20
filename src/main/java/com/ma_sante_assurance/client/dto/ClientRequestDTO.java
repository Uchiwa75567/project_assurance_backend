package com.ma_sante_assurance.client.dto;

import com.ma_sante_assurance.common.enums.GeneralStatus;
import com.ma_sante_assurance.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class ClientRequestDTO {

    public record Create(
            String userId,
            @NotBlank String prenom,
            @NotBlank String nom,
            LocalDate dateNaissance,
            @NotBlank
            @Pattern(regexp = "[+0-9][0-9\\s-]{6,20}", message = ValidationMessages.PHONE_INVALID)
            String telephone,
            String adresse,
            @Pattern(regexp = "^$|[A-Z0-9-]{5,30}$", message = ValidationMessages.CNI_INVALID)
            String numeroCni,
            String photoUrl,
            String typeAssurance,
            GeneralStatus statut,
            String createdByAgentId
    ) {
    }

    public record CreateFromUser(
            @NotBlank String userId,
            @NotBlank String fullName,
            LocalDate dateNaissance,
            String telephone,
            String numeroCni,
            String photoUrl
    ) {
    }

    public record Update(
            String prenom,
            String nom,
            LocalDate dateNaissance,
            @Pattern(regexp = "[+0-9][0-9\\s-]{6,20}", message = ValidationMessages.PHONE_INVALID)
            String telephone,
            String adresse,
            @Pattern(regexp = "^$|[A-Z0-9-]{5,30}$", message = ValidationMessages.CNI_INVALID)
            String numeroCni,
            String photoUrl,
            String typeAssurance,
            GeneralStatus statut
    ) {
    }
}
