package com.ma_sante_assurance.paiement.entity;

import com.ma_sante_assurance.common.enums.PaiementStatus;
import com.ma_sante_assurance.souscription.entity.Souscription;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "souscription_id")
    private Souscription souscription;

    @Column(nullable = false)
    private BigDecimal montant;
    
    @Column
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaiementStatus statut;

    @Column
    private String provider;

    @Column
    private String transactionId;

    @Column
    private String paymentUrl;

    @Column(nullable = false)
    private Instant dateCreation;

    @Column
    private Instant dateValidation;

    @Column
    private LocalDate periodDebut;

    @Column
    private LocalDate periodFin;

    @PrePersist
    void prePersist() {
        if (dateCreation == null) dateCreation = Instant.now();
        if (statut == null) statut = PaiementStatus.EN_ATTENTE;
    }
}
