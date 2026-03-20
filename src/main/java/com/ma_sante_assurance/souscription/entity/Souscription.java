package com.ma_sante_assurance.souscription.entity;

import com.ma_sante_assurance.common.enums.SouscriptionStatus;
import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.client.entity.Client;
import com.ma_sante_assurance.agent.entity.Agent;
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

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "souscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Souscription {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pack_id")
    private Pack pack;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column
    private LocalDate dateFin;

    @Column
    private LocalDate dateProchainPaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SouscriptionStatus statut;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (statut == null) statut = SouscriptionStatus.ACTIVE;
    }
}
