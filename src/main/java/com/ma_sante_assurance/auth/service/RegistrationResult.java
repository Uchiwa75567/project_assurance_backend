package com.ma_sante_assurance.auth.service;

import com.ma_sante_assurance.client.dto.ClientResponseDTO;
import com.ma_sante_assurance.user.entity.User;

public record RegistrationResult(
        User user,
        ClientResponseDTO clientProfile
) {
}
