package com.ma_sante_assurance.packgarantie.entity;

import com.ma_sante_assurance.garantie.entity.Garantie;
import com.ma_sante_assurance.pack.entity.Pack;
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
@Table(name = "pack_garanties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackGarantie {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pack_id")
    private Pack pack;

    @ManyToOne(optional = false)
    @JoinColumn(name = "garantie_id")
    private Garantie garantie;

    @Column
    private BigDecimal plafondSpecifique;
}
