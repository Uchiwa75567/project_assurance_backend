package com.ma_sante_assurance.client.entity;

import com.ma_sante_assurance.common.enums.GeneralStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    private String id;

    @Column(unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    private String numeroAssurance;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column
    private LocalDate dateNaissance;

    @Column(nullable = false)
    private String telephone;

    @Column
    private String adresse;

    @Column
    private String numeroCni;

    @Column
    private String photoUrl;

    @Column
    private String typeAssurance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeneralStatus statut;

    @Column
    private String createdByAgentId;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (statut == null) statut = GeneralStatus.ACTIVE;
    }
}
