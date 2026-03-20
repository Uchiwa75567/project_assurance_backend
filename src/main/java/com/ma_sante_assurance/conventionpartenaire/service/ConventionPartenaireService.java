package com.ma_sante_assurance.conventionpartenaire.service;

import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.conventionpartenaire.dto.ConventionPartenaireRequestDTO;
import com.ma_sante_assurance.conventionpartenaire.dto.ConventionPartenaireResponseDTO;
import com.ma_sante_assurance.conventionpartenaire.entity.ConventionPartenaire;
import com.ma_sante_assurance.conventionpartenaire.repository.ConventionPartenaireRepository;
import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.pack.repository.PackRepository;
import com.ma_sante_assurance.partenaire.entity.Partenaire;
import com.ma_sante_assurance.partenaire.repository.PartenaireRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConventionPartenaireService {

    private final ConventionPartenaireRepository conventionRepository;
    private final PackRepository packRepository;
    private final PartenaireRepository partenaireRepository;

    public ConventionPartenaireService(ConventionPartenaireRepository conventionRepository,
                                       PackRepository packRepository,
                                       PartenaireRepository partenaireRepository) {
        this.conventionRepository = conventionRepository;
        this.packRepository = packRepository;
        this.partenaireRepository = partenaireRepository;
    }

    @Transactional
    public ConventionPartenaireResponseDTO create(ConventionPartenaireRequestDTO request) {
        Pack pack = packRepository.findById(request.packId())
                .orElseThrow(() -> new EntityNotFoundException("Pack introuvable"));
        Partenaire partenaire = partenaireRepository.findById(request.partenaireId())
                .orElseThrow(() -> new EntityNotFoundException("Partenaire introuvable"));

        ConventionPartenaire convention = ConventionPartenaire.builder()
                .id(IdGenerator.uuid())
                .pack(pack)
                .partenaire(partenaire)
                .acceptee(request.acceptee())
                .tauxCouverture(request.tauxCouverture())
                .plafond(request.plafond())
                .actif(request.actif() != null ? request.actif() : true)
                .build();

        return toDto(conventionRepository.save(convention));
    }

    @Transactional(readOnly = true)
    public List<ConventionPartenaireResponseDTO> listByPack(String packId) {
        return conventionRepository.findByPack_Id(packId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ConventionPartenaireResponseDTO> listByPartenaire(String partenaireId) {
        return conventionRepository.findByPartenaire_Id(partenaireId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ConventionPartenaireResponseDTO findById(String id) {
        return conventionRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Convention introuvable"));
    }

    @Transactional
    public ConventionPartenaireResponseDTO update(String id, ConventionPartenaireRequestDTO request) {
        ConventionPartenaire convention = conventionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Convention introuvable"));

        if (request.packId() != null && !request.packId().isBlank()) {
            Pack pack = packRepository.findById(request.packId())
                    .orElseThrow(() -> new EntityNotFoundException("Pack introuvable"));
            convention.setPack(pack);
        }

        if (request.partenaireId() != null && !request.partenaireId().isBlank()) {
            Partenaire partenaire = partenaireRepository.findById(request.partenaireId())
                    .orElseThrow(() -> new EntityNotFoundException("Partenaire introuvable"));
            convention.setPartenaire(partenaire);
        }

        if (request.acceptee() != null) {
            convention.setAcceptee(request.acceptee());
        }

        if (request.tauxCouverture() != null) {
            convention.setTauxCouverture(request.tauxCouverture());
        }

        if (request.plafond() != null) {
            convention.setPlafond(request.plafond());
        }

        if (request.actif() != null) {
            convention.setActif(request.actif());
        }

        return toDto(conventionRepository.save(convention));
    }

    @Transactional
    public void delete(String id) {
        if (!conventionRepository.existsById(id)) {
            throw new EntityNotFoundException("Convention introuvable");
        }
        conventionRepository.deleteById(id);
    }

    private ConventionPartenaireResponseDTO toDto(ConventionPartenaire convention) {
        return new ConventionPartenaireResponseDTO(
                convention.getId(),
                convention.getPack().getId(),
                convention.getPack().getNom(),
                convention.getPartenaire().getId(),
                convention.getPartenaire().getNom(),
                convention.getAcceptee(),
                convention.getTauxCouverture(),
                convention.getPlafond(),
                convention.getActif()
        );
    }
}
