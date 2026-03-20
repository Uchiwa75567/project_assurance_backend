package com.ma_sante_assurance.paiement.dto;

import com.ma_sante_assurance.common.enums.PaiementStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PaiementResponseDTO(
        String id,
        String souscriptionId,
        BigDecimal montant,
        String reference,
        PaiementStatus statut,
        String provider,
        String transactionId,
        String paymentUrl,
        Instant dateCreation,
        Instant dateValidation,
        LocalDate periodDebut,
        LocalDate periodFin
) {
}
