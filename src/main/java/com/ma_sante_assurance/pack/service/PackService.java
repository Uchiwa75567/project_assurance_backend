package com.ma_sante_assurance.pack.service;

import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.pack.dto.PackRequestDTO;
import com.ma_sante_assurance.pack.dto.PackResponseDTO;
import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.pack.repository.PackRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PackService {

    private final PackRepository packRepository;

    public PackService(PackRepository packRepository) {
        this.packRepository = packRepository;
    }

    @Transactional
    public PackResponseDTO create(PackRequestDTO request) {
        Pack pack = Pack.builder()
                .id(IdGenerator.uuid())
                .code(request.code())
                .nom(request.nom())
                .description(request.description())
                .prix(request.prix())
                .duree(request.duree())
                .actif(request.actif())
                .build();
        return toDto(packRepository.save(pack));
    }

    @Transactional(readOnly = true)
    public List<PackResponseDTO> list() {
        return packRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PackResponseDTO findById(String id) {
        return packRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Pack introuvable"));
    }

    @Transactional
    public PackResponseDTO update(String id, PackRequestDTO request) {
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pack introuvable"));

        if (request.code() != null) pack.setCode(request.code());
        if (request.nom() != null) pack.setNom(request.nom());
        if (request.description() != null) pack.setDescription(request.description());
        if (request.prix() != null) pack.setPrix(request.prix());
        if (request.duree() != null) pack.setDuree(request.duree());
        if (request.actif() != null) pack.setActif(request.actif());

        return toDto(packRepository.save(pack));
    }

    @Transactional
    public void delete(String id) {
        if (!packRepository.existsById(id)) {
            throw new EntityNotFoundException("Pack introuvable");
        }
        packRepository.deleteById(id);
    }

    private PackResponseDTO toDto(Pack pack) {
        return new PackResponseDTO(
                pack.getId(),
                pack.getCode(),
                pack.getNom(),
                pack.getDescription(),
                pack.getPrix(),
                pack.getDuree(),
                pack.getActif()
        );
    }
}
