package com.ma_sante_assurance.user.dto;

import com.ma_sante_assurance.common.enums.UserRole;

public record UserResponseDTO(
        String id,
        String fullName,
        String email,
        UserRole role,
        Boolean actif
) {
}
