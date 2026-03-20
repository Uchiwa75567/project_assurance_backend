package com.ma_sante_assurance.paiement.dto;

import com.ma_sante_assurance.common.ApiResponse;

public record PaydunyaInitResponse(
    String paymentUrl,
    String transactionId,
    String souscriptionId
) {}
