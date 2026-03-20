package com.ma_sante_assurance.paiement.dto;

public record IPNRequest(
    String token,
    String status,
    String amount,
    String provider,
    String transactionId,
    String customerPhone
) {}
