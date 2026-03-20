package com.ma_sante_assurance.carte.repository;

import com.ma_sante_assurance.carte.entity.Carte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarteRepository extends JpaRepository<Carte, String> {
    Optional<Carte> findBySouscription_Id(String souscriptionId);
}
