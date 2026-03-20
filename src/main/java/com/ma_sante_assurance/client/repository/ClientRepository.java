package com.ma_sante_assurance.client.repository;

import com.ma_sante_assurance.client.entity.Client;
import com.ma_sante_assurance.common.enums.GeneralStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByNumeroAssurance(String numeroAssurance);
    Optional<Client> findByUserId(String userId);

    @Query("""
            select c from Client c
            where (:search is null or :search = ''
                   or lower(c.prenom) like lower(concat('%', :search, '%'))
                   or lower(c.nom) like lower(concat('%', :search, '%'))
                   or lower(c.numeroAssurance) like lower(concat('%', :search, '%')))
              and (:statut is null or c.statut = :statut)
            """)
    Page<Client> search(String search, GeneralStatus statut, Pageable pageable);
}
