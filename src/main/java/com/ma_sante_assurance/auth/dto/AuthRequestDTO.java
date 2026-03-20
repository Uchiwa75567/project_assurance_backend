package com.ma_sante_assurance.auth.dto;

import com.ma_sante_assurance.common.enums.UserRole;
import com.ma_sante_assurance.common.validation.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class AuthRequestDTO {

    public record RegisterRequest(
            @NotBlank String fullName,
            @Email(message = ValidationMessages.EMAIL_INVALID) String email,
            LocalDate dateNaissance,
            @Pattern(regexp = "[+0-9][0-9\\s-]{6,20}", message = ValidationMessages.PHONE_INVALID) String telephone,
            String numeroCni,
            String photoUrl,
            @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED) String password,
            UserRole role
    ) {
    }

    public record LoginRequest(
            @NotBlank(message = ValidationMessages.IDENTIFIER_REQUIRED) String identifier,
            @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED) String password
    ) {
    }

    public record RefreshRequest(
            @NotBlank String refreshToken
    ) {
    }
}
