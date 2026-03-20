package com.ma_sante_assurance.conventionpartenaire.entity;

import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.partenaire.entity.Partenaire;
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

import java.math.BigDecimal;

@Entity
@Table(name = "conventions_partenaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConventionPartenaire {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pack_id")
    private Pack pack;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partenaire_id")
    private Partenaire partenaire;

    @Column(nullable = false)
    private Boolean acceptee;

    @Column
    private BigDecimal tauxCouverture;

    @Column
    private BigDecimal plafond;

    @Column(nullable = false)
    private Boolean actif;
}
