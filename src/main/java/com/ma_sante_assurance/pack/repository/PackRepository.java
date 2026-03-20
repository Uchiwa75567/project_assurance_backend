package com.ma_sante_assurance.pack.repository;

import com.ma_sante_assurance.pack.entity.Pack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackRepository extends JpaRepository<Pack, String> {
    Optional<Pack> findByCode(String code);
}
