package com.ma_sante_assurance.paiement.service;

import com.ma_sante_assurance.carte.dto.CarteRequestDTO;
import com.ma_sante_assurance.carte.service.CarteService;
import com.ma_sante_assurance.client.entity.Client;
import com.ma_sante_assurance.client.repository.ClientRepository;
import com.ma_sante_assurance.common.audit.service.AuditService;
import com.ma_sante_assurance.common.enums.CarteStatus;
import com.ma_sante_assurance.common.enums.PaiementStatus;
import com.ma_sante_assurance.common.enums.SouscriptionStatus;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.pack.repository.PackRepository;
import com.ma_sante_assurance.paiement.dto.IPNRequest;
import com.ma_sante_assurance.paiement.dto.PaiementRequestDTO;
import com.ma_sante_assurance.paiement.dto.PaiementResponseDTO;
import com.ma_sante_assurance.paiement.dto.PaydunyaInitRequest;
import com.ma_sante_assurance.paiement.dto.PaydunyaInitResponse;
import com.ma_sante_assurance.paiement.entity.Paiement;
import com.ma_sante_assurance.paiement.repository.PaiementRepository;
import com.ma_sante_assurance.paiement.service.PayDunyaPaymentService;
import com.ma_sante_assurance.souscription.entity.Souscription;
import com.ma_sante_assurance.souscription.repository.SouscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final SouscriptionRepository souscriptionRepository;
    private final AuditService auditService;
    private final PayDunyaPaymentService payDunyaService;
    private final CarteService carteService;
    private final ClientRepository clientRepository;
    private final PackRepository packRepository;

    public PaiementService(PaiementRepository paiementRepository,
                           SouscriptionRepository souscriptionRepository,
                           AuditService auditService,
                           PayDunyaPaymentService payDunyaService,
                           CarteService carteService,
                           ClientRepository clientRepository,
                           PackRepository packRepository) {
        this.paiementRepository = paiementRepository;
        this.souscriptionRepository = souscriptionRepository;
        this.auditService = auditService;
        this.payDunyaService = payDunyaService;
        this.carteService = carteService;
        this.clientRepository = clientRepository;
        this.packRepository = packRepository;
    }

    @Transactional
    public PaydunyaInitResponse initierPaiement(PaydunyaInitRequest request, String actorUserId) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + request.clientId()));
        Pack pack = packRepository.findById(request.packId())
                .orElseThrow(() -> new EntityNotFoundException("Pack introuvable: " + request.packId()));

        Souscription souscription = Souscription.builder()
                .id(IdGenerator.uuid())
                .client(client)
                .pack(pack)
                .statut(SouscriptionStatus.EN_ATTENTE)
                .build();
        souscription = souscriptionRepository.save(souscription);
        auditService.log(actorUserId, "SOUSCRIPTION_CREATE_ATTENTE", "SOUSCRIPTION", souscription.getId(), "PayDunya init");

        Paiement paiement = Paiement.builder()
                .id(IdGenerator.uuid())
                .souscription(souscription)
                .montant(pack.getPrix())
                .reference(souscription.getId())
                .statut(PaiementStatus.EN_ATTENTE)
                .provider("PAYDUNYA")
                .build();
        paiementRepository.save(paiement);

        var paydunyaResponse = payDunyaService.createInvoice(request.clientId(), request.packId(), souscription.getId());

        paiement.setPaymentUrl(paydunyaResponse.paymentUrl());
        paiement.setTransactionId(paydunyaResponse.token());
        paiementRepository.save(paiement);

        auditService.log(actorUserId, "PAIEMENT_PAYDUNYA_INIT", "PAIEMENT", paiement.getId(), paydunyaResponse.paymentUrl());

        return new PaydunyaInitResponse(
                paydunyaResponse.paymentUrl(),
                paydunyaResponse.token(),
                souscription.getId()
        );
    }

    @Transactional
    public void handleIPN(IPNRequest request) {
        log.info("IPN reçu: token={}, status={}, amount={}", request.token(), request.status(), request.amount());

        Paiement paiement = paiementRepository.findByReference(request.token())
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable pour token: " + request.token()));

        Souscription souscription = paiement.getSouscription();

        if ("completed".equals(request.status())) {
            var verification = payDunyaService.verifyPayment(request.token());
            String verifyStatus = (String) verification.get("status");
            if (!"completed".equals(verifyStatus)) {
                log.warn("IPN OK mais verify KO: {}", verification);
                return;
            }

            paiement.setStatut(PaiementStatus.VALIDE);
            paiement.setProvider(request.provider());
            paiement.setTransactionId(request.transactionId());
            paiement.setDateValidation(Instant.now());
            paiementRepository.save(paiement);

            souscription.setStatut(SouscriptionStatus.ACTIVE);
            souscription.setDateDebut(LocalDate.now());
            souscription.setDateFin(LocalDate.now().plusMonths(1));
            souscriptionRepository.save(souscription);

            carteService.createOrUpdate(new CarteRequestDTO(souscription.getId(), null, null, null, null, CarteStatus.ACTIVATED));

            auditService.log("system", "PAIEMENT_PAYDUNYA_SUCCESS", "PAIEMENT", paiement.getId(), request.token());
            log.info("PayDunya succès complet: paiement={}, souscription={}, carte générée", paiement.getId(), souscription.getId());

        } else {
            paiement.setStatut(PaiementStatus.ECHEC);
            paiementRepository.save(paiement);
            auditService.log("system", "PAIEMENT_PAYDUNYA_FAILED", "PAIEMENT", paiement.getId(), request.status());
            log.warn("PayDunya IPN échec: {}", request.status());
        }
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
