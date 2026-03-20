package com.ma_sante_assurance.beneficiaire.repository;

import com.ma_sante_assurance.beneficiaire.entity.Beneficiaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaireRepository extends JpaRepository<Beneficiaire, String> {
    List<Beneficiaire> findBySouscription_Id(String souscriptionId);
}
