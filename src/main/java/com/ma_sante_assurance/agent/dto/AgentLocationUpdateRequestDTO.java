package com.ma_sante_assurance.agent.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgentLocationUpdateRequestDTO(
        @NotNull @Min(-90) @Max(90) Double latitude,
        @NotNull @Min(-180) @Max(180) Double longitude,
        @NotNull @Min(0) Double speedKmh
) {
}
