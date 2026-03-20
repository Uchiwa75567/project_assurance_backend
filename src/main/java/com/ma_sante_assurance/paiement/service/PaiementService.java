package com.ma_sante_assurance.paiement.service;

import com.ma_sante_assurance.common.audit.service.AuditService;
import com.ma_sante_assurance.common.enums.PaiementStatus;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.paiement.dto.PaiementRequestDTO;
import com.ma_sante_assurance.paiement.dto.PaiementResponseDTO;
import com.ma_sante_assurance.paiement.entity.Paiement;
import com.ma_sante_assurance.paiement.repository.PaiementRepository;
import com.ma_sante_assurance.souscription.entity.Souscription;
import com.ma_sante_assurance.souscription.repository.SouscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final SouscriptionRepository souscriptionRepository;
    private final AuditService auditService;

    public PaiementService(PaiementRepository paiementRepository,
                           SouscriptionRepository souscriptionRepository,
                           AuditService auditService) {
        this.paiementRepository = paiementRepository;
        this.souscriptionRepository = souscriptionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public PaiementResponseDTO create(PaiementRequestDTO.Create request, String actorUserId) {
        if (request.reference() != null && !request.reference().isBlank()) {
            Paiement existing = paiementRepository.findByReference(request.reference()).orElse(null);
            if (existing != null) {
                return toDto(existing);
            }
        }

        Souscription souscription = souscriptionRepository.findById(request.souscriptionId())
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable"));

        Paiement paiement = Paiement.builder()
                .id(IdGenerator.uuid())
                .souscription(souscription)
                .montant(request.montant())
                .reference(request.reference())
                .provider(request.provider())
                .transactionId(request.transactionId())
                .paymentUrl(request.paymentUrl())
                .periodDebut(request.periodDebut())
                .periodFin(request.periodFin())
                .statut(PaiementStatus.EN_ATTENTE)
                .build();

        Paiement saved = paiementRepository.save(paiement);
        auditService.log(actorUserId, "PAIEMENT_CREATE", "PAIEMENT", saved.getId(), saved.getReference());
        return toDto(saved);
    }

    @Transactional
    public PaiementResponseDTO update(String id, PaiementRequestDTO.Update request, String actorUserId) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));

        if (request.statut() != null) {
            PaiementStatus current = paiement.getStatut();
            PaiementStatus next = request.statut();

            boolean valid = (current == PaiementStatus.EN_ATTENTE && (next == PaiementStatus.VALIDE || next == PaiementStatus.ECHEC))
                    || current == next;

            if (!valid) {
                throw new IllegalArgumentException("Transition paiement non autorisee: " + current + " -> " + next);
            }

            paiement.setStatut(next);
            if (next == PaiementStatus.VALIDE) {
                paiement.setDateValidation(Instant.now());
            }
        }

        if (request.reference() != null) {
            paiement.setReference(request.reference());
        }

        if (request.provider() != null) {
            paiement.setProvider(request.provider());
        }

        if (request.transactionId() != null) {
            paiement.setTransactionId(request.transactionId());
        }

        if (request.paymentUrl() != null) {
            paiement.setPaymentUrl(request.paymentUrl());
        }

        if (request.periodDebut() != null) {
            paiement.setPeriodDebut(request.periodDebut());
        }

        if (request.periodFin() != null) {
            paiement.setPeriodFin(request.periodFin());
        }

        Paiement saved = paiementRepository.save(paiement);
        auditService.log(actorUserId, "PAIEMENT_UPDATE", "PAIEMENT", saved.getId(), saved.getStatut().name());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponseDTO> list() {
        return paiementRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PaiementResponseDTO findById(String id) {
        return paiementRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));
    }

    @Transactional
    public void delete(String id) {
        if (!paiementRepository.existsById(id)) {
            throw new EntityNotFoundException("Paiement introuvable");
        }
        paiementRepository.deleteById(id);
    }

    private PaiementResponseDTO toDto(Paiement p) {
        return new PaiementResponseDTO(
                p.getId(),
                p.getSouscription().getId(),
                p.getMontant(),
                p.getReference(),
                p.getStatut(),
                p.getProvider(),
                p.getTransactionId(),
                p.getPaymentUrl(),
                p.getDateCreation(),
                p.getDateValidation(),
                p.getPeriodDebut(),
                p.getPeriodFin()
        );
    }
}
