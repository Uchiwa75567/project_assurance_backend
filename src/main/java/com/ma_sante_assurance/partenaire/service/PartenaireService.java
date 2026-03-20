package com.ma_sante_assurance.partenaire.service;

import com.ma_sante_assurance.common.util.GeoUtil;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.partenaire.dto.PartenaireRequestDTO;
import com.ma_sante_assurance.partenaire.dto.PartenaireResponseDTO;
import com.ma_sante_assurance.partenaire.entity.Partenaire;
import com.ma_sante_assurance.partenaire.repository.PartenaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class PartenaireService {

    private final PartenaireRepository partenaireRepository;

    public PartenaireService(PartenaireRepository partenaireRepository) {
        this.partenaireRepository = partenaireRepository;
    }

    @Transactional
    public PartenaireResponseDTO create(PartenaireRequestDTO request) {
        Partenaire partenaire = Partenaire.builder()
                .id(IdGenerator.uuid())
                .userId(request.userId())
                .nom(request.nom())
                .type(request.type())
                .adresse(request.adresse())
                .telephone(request.telephone())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .actif(request.actif())
                .build();
        return toDto(partenaireRepository.save(partenaire), null);
    }

    @Transactional(readOnly = true)
    public List<PartenaireResponseDTO> list(Double lat, Double lon) {
        return partenaireRepository.findByActifTrue().stream()
                .map(p -> toDto(
                        p,
                        lat != null && lon != null ? GeoUtil.distanceKm(lat, lon, p.getLatitude(), p.getLongitude()) : null
                ))
                .sorted(Comparator.comparing(
                        dto -> dto.distanceKm() == null ? Double.MAX_VALUE : dto.distanceKm()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public PartenaireResponseDTO findById(String id) {
        Partenaire partenaire = partenaireRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Partenaire introuvable"));
        return toDto(partenaire, null);
    }

    @Transactional
    public PartenaireResponseDTO update(String id, PartenaireRequestDTO request) {
        Partenaire partenaire = partenaireRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Partenaire introuvable"));

        if (request.userId() != null) partenaire.setUserId(request.userId());
        if (request.nom() != null) partenaire.setNom(request.nom());
        if (request.type() != null) partenaire.setType(request.type());
        if (request.adresse() != null) partenaire.setAdresse(request.adresse());
        if (request.telephone() != null) partenaire.setTelephone(request.telephone());
        if (request.latitude() != null) partenaire.setLatitude(request.latitude());
        if (request.longitude() != null) partenaire.setLongitude(request.longitude());
        if (request.actif() != null) partenaire.setActif(request.actif());

        return toDto(partenaireRepository.save(partenaire), null);
    }

    @Transactional
    public void delete(String id) {
        if (!partenaireRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Partenaire introuvable");
        }
        partenaireRepository.deleteById(id);
    }

    private PartenaireResponseDTO toDto(Partenaire p, Double distanceKm) {
        return new PartenaireResponseDTO(
                p.getId(), p.getUserId(), p.getNom(), p.getType(), p.getAdresse(), p.getTelephone(),
                p.getLatitude(), p.getLongitude(), distanceKm, p.getActif()
        );
    }
}
