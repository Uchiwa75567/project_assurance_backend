package com.ma_sante_assurance.user.dto;

import com.ma_sante_assurance.common.enums.UserRole;
import com.ma_sante_assurance.common.validation.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotBlank String fullName,
        @NotBlank @Email(message = ValidationMessages.EMAIL_INVALID) String email,
        @NotBlank String password,
        @NotNull UserRole role
) {
}
