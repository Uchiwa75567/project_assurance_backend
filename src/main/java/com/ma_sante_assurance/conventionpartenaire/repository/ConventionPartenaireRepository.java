package com.ma_sante_assurance.conventionpartenaire.repository;

import com.ma_sante_assurance.conventionpartenaire.entity.ConventionPartenaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConventionPartenaireRepository extends JpaRepository<ConventionPartenaire, String> {
    List<ConventionPartenaire> findByPack_Id(String packId);
    List<ConventionPartenaire> findByPartenaire_Id(String partenaireId);
}
