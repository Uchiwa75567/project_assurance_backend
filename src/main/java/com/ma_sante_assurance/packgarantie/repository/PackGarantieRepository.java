package com.ma_sante_assurance.packgarantie.repository;

import com.ma_sante_assurance.packgarantie.entity.PackGarantie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackGarantieRepository extends JpaRepository<PackGarantie, String> {
    List<PackGarantie> findByPack_Id(String packId);
    List<PackGarantie> findByGarantie_Id(String garantieId);
    Optional<PackGarantie> findByPack_IdAndGarantie_Id(String packId, String garantieId);
}
