package com.ma_sante_assurance.carte.service;

import com.ma_sante_assurance.carte.dto.CarteRequestDTO;
import com.ma_sante_assurance.carte.dto.CarteResponseDTO;
import com.ma_sante_assurance.carte.entity.Carte;
import com.ma_sante_assurance.carte.repository.CarteRepository;
import com.ma_sante_assurance.common.enums.CarteStatus;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.souscription.entity.Souscription;
import com.ma_sante_assurance.souscription.repository.SouscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@Service
public class CarteService {

    private final CarteRepository carteRepository;
    private final SouscriptionRepository souscriptionRepository;
    private final Random random = new Random();

    public CarteService(CarteRepository carteRepository, SouscriptionRepository souscriptionRepository) {
        this.carteRepository = carteRepository;
        this.souscriptionRepository = souscriptionRepository;
    }

    @Transactional
    public CarteResponseDTO createOrUpdate(CarteRequestDTO request) {
        Souscription souscription = souscriptionRepository.findById(request.souscriptionId())
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable"));

        Carte carte = carteRepository.findBySouscription_Id(request.souscriptionId())
                .orElse(Carte.builder().id(IdGenerator.uuid()).souscription(souscription).build());

        carte.setNumeroCarte(request.numeroCarte() == null ? generateNumeroCarte() : request.numeroCarte());
        carte.setDateEmission(request.dateEmission() == null ? LocalDate.now() : request.dateEmission());
        carte.setDateExpiration(request.dateExpiration() == null ? LocalDate.now().plusYears(1) : request.dateExpiration());
        carte.setQrCode(request.qrCode());
        carte.setStatut(request.statut() == null ? CarteStatus.ACTIVATED : request.statut());

        return toDto(carteRepository.save(carte));
    }

    @Transactional(readOnly = true)
    public CarteResponseDTO getBySouscription(String souscriptionId) {
        Carte carte = carteRepository.findBySouscription_Id(souscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable"));
        return toDto(carte);
    }

    private String generateNumeroCarte() {
        return "CARTE-" + (100000 + random.nextInt(900000));
    }

    private CarteResponseDTO toDto(Carte carte) {
        return new CarteResponseDTO(
                carte.getId(),
                carte.getSouscription().getId(),
                carte.getNumeroCarte(),
                carte.getDateEmission(),
                carte.getDateExpiration(),
                carte.getQrCode(),
                carte.getStatut()
        );
    }
}
