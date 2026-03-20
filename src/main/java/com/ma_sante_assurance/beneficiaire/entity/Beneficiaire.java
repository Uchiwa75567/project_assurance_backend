package com.ma_sante_assurance.beneficiaire.entity;

import com.ma_sante_assurance.souscription.entity.Souscription;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "beneficiaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiaire {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "souscription_id")
    private Souscription souscription;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column
    private LocalDate dateNaissance;

    @Column
    private String lien;

    @Column(nullable = false)
    private Boolean isPrincipal;
}
