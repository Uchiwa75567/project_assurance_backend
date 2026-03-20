package com.ma_sante_assurance.paiement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaydunyaInitRequest(
    @NotBlank(message = "Client ID requis")
    String clientId,
    
    @NotBlank(message = "Pack ID requis")
    String packId
) {}
