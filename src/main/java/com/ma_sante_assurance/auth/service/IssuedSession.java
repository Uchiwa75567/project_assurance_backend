package com.ma_sante_assurance.auth.service;

import com.ma_sante_assurance.auth.dto.AuthResponseDTO;

public record IssuedSession(
        String accessToken,
        String refreshToken,
        AuthResponseDTO.SessionResponse session
) {
}
