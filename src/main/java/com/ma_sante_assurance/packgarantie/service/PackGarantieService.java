package com.ma_sante_assurance.packgarantie.service;

import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.garantie.entity.Garantie;
import com.ma_sante_assurance.garantie.repository.GarantieRepository;
import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.pack.repository.PackRepository;
import com.ma_sante_assurance.packgarantie.dto.PackGarantieRequestDTO;
import com.ma_sante_assurance.packgarantie.dto.PackGarantieResponseDTO;
import com.ma_sante_assurance.packgarantie.entity.PackGarantie;
import com.ma_sante_assurance.packgarantie.repository.PackGarantieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PackGarantieService {

    private final PackGarantieRepository packGarantieRepository;
    private final PackRepository packRepository;
    private final GarantieRepository garantieRepository;

    public PackGarantieService(PackGarantieRepository packGarantieRepository,
                               PackRepository packRepository,
                               GarantieRepository garantieRepository) {
        this.packGarantieRepository = packGarantieRepository;
        this.packRepository = packRepository;
        this.garantieRepository = garantieRepository;
    }

    @Transactional
    public PackGarantieResponseDTO create(PackGarantieRequestDTO request) {
        Pack pack = packRepository.findById(request.packId())
                .orElseThrow(() -> new EntityNotFoundException("Pack introuvable"));
        Garantie garantie = garantieRepository.findById(request.garantieId())
                .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable"));

        PackGarantie packGarantie = PackGarantie.builder()
                .id(IdGenerator.uuid())
                .pack(pack)
                .garantie(garantie)
                .plafondSpecifique(request.plafondSpecifique())
                .build();

        return toDto(packGarantieRepository.save(packGarantie));
    }

    @Transactional(readOnly = true)
    public List<PackGarantieResponseDTO> listByPack(String packId) {
        return packGarantieRepository.findByPack_Id(packId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PackGarantieResponseDTO> listByGarantie(String garantieId) {
        return packGarantieRepository.findByGarantie_Id(garantieId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public PackGarantieResponseDTO update(String id, PackGarantieRequestDTO request) {
        PackGarantie existing = packGarantieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Association introuvable"));

        if (request.packId() != null && !request.packId().isBlank()) {
            Pack pack = packRepository.findById(request.packId())
                    .orElseThrow(() -> new EntityNotFoundException("Pack introuvable"));
            existing.setPack(pack);
        }

        if (request.garantieId() != null && !request.garantieId().isBlank()) {
            Garantie garantie = garantieRepository.findById(request.garantieId())
                    .orElseThrow(() -> new EntityNotFoundException("Garantie introuvable"));
            existing.setGarantie(garantie);
        }

        if (request.plafondSpecifique() != null) {
            existing.setPlafondSpecifique(request.plafondSpecifique());
        }

        return toDto(packGarantieRepository.save(existing));
    }

    @Transactional
    public void delete(String id) {
        if (!packGarantieRepository.existsById(id)) {
            throw new EntityNotFoundException("Association introuvable");
        }
        packGarantieRepository.deleteById(id);
    }

    private PackGarantieResponseDTO toDto(PackGarantie packGarantie) {
        Garantie garantie = packGarantie.getGarantie();
        return new PackGarantieResponseDTO(
                packGarantie.getId(),
                packGarantie.getPack().getId(),
                garantie.getId(),
                garantie.getLibelle(),
                garantie.getDescription(),
                garantie.getPlafond(),
                packGarantie.getPlafondSpecifique()
        );
    }
}
