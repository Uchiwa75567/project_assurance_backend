package com.ma_sante_assurance.garantie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "garanties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Garantie {

    @Id
    private String id;

    @Column(nullable = false)
    private String libelle;

    @Column
    private String description;

    @Column
    private BigDecimal plafond;
}
