package com.ma_sante_assurance.paiement.dto;

import com.ma_sante_assurance.common.enums.PaiementStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaiementRequestDTO {

    public record Create(
            @NotBlank String souscriptionId,
            @NotNull BigDecimal montant,
            String reference,
            String provider,
            String transactionId,
            String paymentUrl,
            LocalDate periodDebut,
            LocalDate periodFin
    ) {
    }

    public record Update(
            PaiementStatus statut,
            String reference,
            String provider,
            String transactionId,
            String paymentUrl,
            LocalDate periodDebut,
            LocalDate periodFin
    ) {
    }
}
