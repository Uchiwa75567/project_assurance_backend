package com.ma_sante_assurance.garantie.service;

import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.garantie.dto.GarantieRequestDTO;
import com.ma_sante_assurance.garantie.dto.GarantieResponseDTO;
import com.ma_sante_assurance.garantie.entity.Garantie;
import com.ma_sante_assurance.garantie.repository.GarantieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GarantieService {

    private final GarantieRepository garantieRepository;

    public GarantieService(GarantieRepository garantieRepository) {
        this.garantieRepository = garantieRepository;
    }

    @Transactional
    public GarantieResponseDTO create(GarantieRequestDTO request) {
        Garantie garantie = Garantie.builder()
                .id(IdGenerator.uuid())
                .libelle(request.libelle())
                .description(request.description())
                .plafond(request.plafond())
                .build();

        return toDto(garantieRepository.save(garantie));
    }

    @Transactional(readOnly = true)
    public List<GarantieResponseDTO> list() {
        return garantieRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public GarantieResponseDTO findById(String id) {
        Garantie garantie = garantieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable"));
        return toDto(garantie);
    }

    @Transactional
    public GarantieResponseDTO update(String id, GarantieRequestDTO request) {
        Garantie garantie = garantieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable"));

        garantie.setLibelle(request.libelle());
        garantie.setDescription(request.description());
        garantie.setPlafond(request.plafond());

        return toDto(garantieRepository.save(garantie));
    }

    @Transactional
    public void delete(String id) {
        if (!garantieRepository.existsById(id)) {
            throw new EntityNotFoundException("Garantie introuvable");
        }
        garantieRepository.deleteById(id);
    }

    private GarantieResponseDTO toDto(Garantie garantie) {
        return new GarantieResponseDTO(
                garantie.getId(),
                garantie.getLibelle(),
                garantie.getDescription(),
                garantie.getPlafond()
        );
    }
}
