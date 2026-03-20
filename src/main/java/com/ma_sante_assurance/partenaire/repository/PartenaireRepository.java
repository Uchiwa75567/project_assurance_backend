package com.ma_sante_assurance.partenaire.repository;

import com.ma_sante_assurance.partenaire.entity.Partenaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartenaireRepository extends JpaRepository<Partenaire, String> {
    List<Partenaire> findByActifTrue();
}
