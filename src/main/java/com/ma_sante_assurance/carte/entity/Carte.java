package com.ma_sante_assurance.carte.entity;

import com.ma_sante_assurance.souscription.entity.Souscription;
import com.ma_sante_assurance.common.enums.CarteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cartes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carte {

    @Id
    private String id;

    @OneToOne(optional = false)
    @JoinColumn(name = "souscription_id", unique = true)
    private Souscription souscription;

    @Column(nullable = false, unique = true)
    private String numeroCarte;

    @Column
    private LocalDate dateEmission;

    @Column
    private LocalDate dateExpiration;

    @Column
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarteStatus statut;
}
