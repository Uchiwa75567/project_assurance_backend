package com.ma_sante_assurance.garantie.repository;

import com.ma_sante_assurance.garantie.entity.Garantie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarantieRepository extends JpaRepository<Garantie, String> {
}
