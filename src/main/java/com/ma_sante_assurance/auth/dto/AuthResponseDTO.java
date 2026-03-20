package com.ma_sante_assurance.auth.dto;

import com.ma_sante_assurance.common.enums.UserRole;

public class AuthResponseDTO {

    public record SessionResponse(
            long accessTokenExpiresIn,
            long refreshTokenExpiresIn,
            String userId,
            String fullName,
            String email,
            UserRole role
    ) {
    }

    public record CsrfResponse(
            String token
    ) {
    }
}
