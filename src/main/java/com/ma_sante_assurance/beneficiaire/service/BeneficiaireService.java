package com.ma_sante_assurance.beneficiaire.service;

import com.ma_sante_assurance.beneficiaire.dto.BeneficiaireRequestDTO;
import com.ma_sante_assurance.beneficiaire.dto.BeneficiaireResponseDTO;
import com.ma_sante_assurance.beneficiaire.entity.Beneficiaire;
import com.ma_sante_assurance.beneficiaire.repository.BeneficiaireRepository;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.souscription.entity.Souscription;
import com.ma_sante_assurance.souscription.repository.SouscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BeneficiaireService {

    private final BeneficiaireRepository beneficiaireRepository;
    private final SouscriptionRepository souscriptionRepository;

    public BeneficiaireService(BeneficiaireRepository beneficiaireRepository,
                               SouscriptionRepository souscriptionRepository) {
        this.beneficiaireRepository = beneficiaireRepository;
        this.souscriptionRepository = souscriptionRepository;
    }

    @Transactional
    public BeneficiaireResponseDTO create(BeneficiaireRequestDTO request) {
        Souscription souscription = souscriptionRepository.findById(request.souscriptionId())
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable"));

        Beneficiaire beneficiaire = Beneficiaire.builder()
                .id(IdGenerator.uuid())
                .souscription(souscription)
                .nom(request.nom())
                .prenom(request.prenom())
                .dateNaissance(request.dateNaissance())
                .lien(request.lien())
                .isPrincipal(request.isPrincipal())
                .build();

        return toDto(beneficiaireRepository.save(beneficiaire));
    }

    @Transactional(readOnly = true)
    public List<BeneficiaireResponseDTO> listBySouscription(String souscriptionId) {
        return beneficiaireRepository.findBySouscription_Id(souscriptionId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public BeneficiaireResponseDTO findById(String id) {
        return beneficiaireRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Beneficiaire introuvable"));
    }

    @Transactional
    public BeneficiaireResponseDTO update(String id, BeneficiaireRequestDTO request) {
        Beneficiaire beneficiaire = beneficiaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beneficiaire introuvable"));

        if (request.souscriptionId() != null && !request.souscriptionId().isBlank()) {
            Souscription souscription = souscriptionRepository.findById(request.souscriptionId())
                    .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable"));
            beneficiaire.setSouscription(souscription);
        }

        if (request.nom() != null) {
            beneficiaire.setNom(request.nom());
        }

        if (request.prenom() != null) {
            beneficiaire.setPrenom(request.prenom());
        }

        if (request.dateNaissance() != null) {
            beneficiaire.setDateNaissance(request.dateNaissance());
        }

        if (request.lien() != null) {
            beneficiaire.setLien(request.lien());
        }

        if (request.isPrincipal() != null) {
            beneficiaire.setIsPrincipal(request.isPrincipal());
        }

        return toDto(beneficiaireRepository.save(beneficiaire));
    }

    @Transactional
    public void delete(String id) {
        if (!beneficiaireRepository.existsById(id)) {
            throw new EntityNotFoundException("Beneficiaire introuvable");
        }
        beneficiaireRepository.deleteById(id);
    }

    private BeneficiaireResponseDTO toDto(Beneficiaire beneficiaire) {
        return new BeneficiaireResponseDTO(
                beneficiaire.getId(),
                beneficiaire.getSouscription().getId(),
                beneficiaire.getNom(),
                beneficiaire.getPrenom(),
                beneficiaire.getDateNaissance(),
                beneficiaire.getLien(),
                beneficiaire.getIsPrincipal()
        );
    }
}
