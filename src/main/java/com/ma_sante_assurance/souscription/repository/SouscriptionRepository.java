package com.ma_sante_assurance.souscription.repository;

import com.ma_sante_assurance.common.enums.SouscriptionStatus;
import com.ma_sante_assurance.souscription.entity.Souscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SouscriptionRepository extends JpaRepository<Souscription, String> {
    List<Souscription> findByClient_Id(String clientId);
    Optional<Souscription> findByClient_IdAndStatut(String clientId, SouscriptionStatus statut);
}
