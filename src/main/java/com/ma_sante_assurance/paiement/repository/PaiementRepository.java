package com.ma_sante_assurance.paiement.repository;

import com.ma_sante_assurance.paiement.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, String> {
    Optional<Paiement> findByReference(String reference);
}
