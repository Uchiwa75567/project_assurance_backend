package com.ma_sante_assurance.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminRequestDTO(
        @NotBlank String id
) {
}
