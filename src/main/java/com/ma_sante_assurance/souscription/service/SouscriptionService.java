package com.ma_sante_assurance.souscription.service;

import com.ma_sante_assurance.client.entity.Client;
import com.ma_sante_assurance.client.repository.ClientRepository;
import com.ma_sante_assurance.agent.entity.Agent;
import com.ma_sante_assurance.agent.repository.AgentRepository;
import com.ma_sante_assurance.common.audit.service.AuditService;
import com.ma_sante_assurance.common.enums.SouscriptionStatus;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.pack.repository.PackRepository;
import com.ma_sante_assurance.souscription.dto.SouscriptionRequestDTO;
import com.ma_sante_assurance.souscription.dto.SouscriptionResponseDTO;
import com.ma_sante_assurance.souscription.entity.Souscription;
import com.ma_sante_assurance.souscription.repository.SouscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SouscriptionService {

    private final SouscriptionRepository souscriptionRepository;
    private final ClientRepository clientRepository;
    private final AgentRepository agentRepository;
    private final PackRepository packRepository;
    private final AuditService auditService;

    public SouscriptionService(SouscriptionRepository souscriptionRepository,
                               ClientRepository clientRepository,
                               AgentRepository agentRepository,
                               PackRepository packRepository,
                               AuditService auditService) {
        this.souscriptionRepository = souscriptionRepository;
        this.clientRepository = clientRepository;
        this.agentRepository = agentRepository;
        this.packRepository = packRepository;
        this.auditService = auditService;
    }

    @Transactional
    public SouscriptionResponseDTO create(SouscriptionRequestDTO.Create request, String actorUserId) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        Agent agent = null;
        if (request.agentId() != null && !request.agentId().isBlank()) {
            agent = agentRepository.findById(request.agentId())
                    .orElseThrow(() -> new EntityNotFoundException("Agent introuvable"));
        }
        Pack pack = packRepository.findById(request.packId())
                .orElseThrow(() -> new EntityNotFoundException("Pack introuvable"));

        souscriptionRepository.findByClient_IdAndStatut(client.getId(), SouscriptionStatus.ACTIVE)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ce client a deja une souscription active");
                });

        Souscription souscription = Souscription.builder()
                .id(IdGenerator.uuid())
                .client(client)
                .agent(agent)
                .pack(pack)
                .dateDebut(request.dateDebut() == null ? LocalDate.now() : request.dateDebut())
                .dateFin(request.dateFin())
                .dateProchainPaiement(request.dateProchainPaiement())
                .statut(request.statut() == null ? SouscriptionStatus.ACTIVE : request.statut())
                .build();

        Souscription saved = souscriptionRepository.save(souscription);
        auditService.log(actorUserId, "SOUSCRIPTION_CREATE", "SOUSCRIPTION", saved.getId(), saved.getStatut().name());
        return toDto(saved);
    }

    @Transactional
    public SouscriptionResponseDTO update(String id, SouscriptionRequestDTO.Update request, String actorUserId) {
        Souscription souscription = souscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable"));

        if (request.clientId() != null && !request.clientId().isBlank()) {
            Client client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
            souscription.setClient(client);
        }

        if (request.agentId() != null && !request.agentId().isBlank()) {
            Agent agent = agentRepository.findById(request.agentId())
                    .orElseThrow(() -> new EntityNotFoundException("Agent introuvable"));
            souscription.setAgent(agent);
        }

        if (request.packId() != null && !request.packId().isBlank()) {
            Pack pack = packRepository.findById(request.packId())
                    .orElseThrow(() -> new EntityNotFoundException("Pack introuvable"));
            souscription.setPack(pack);
        }

        if (request.dateDebut() != null) {
            souscription.setDateDebut(request.dateDebut());
        }

        if (request.dateFin() != null) {
            souscription.setDateFin(request.dateFin());
        }

        if (request.dateProchainPaiement() != null) {
            souscription.setDateProchainPaiement(request.dateProchainPaiement());
        }

        if (request.statut() != null) {
            souscription.setStatut(request.statut());
            if (request.statut() == SouscriptionStatus.EXPIREE && souscription.getDateFin() == null) {
                souscription.setDateFin(LocalDate.now());
            }
        }

        Souscription saved = souscriptionRepository.save(souscription);
        auditService.log(actorUserId, "SOUSCRIPTION_UPDATE", "SOUSCRIPTION", saved.getId(), saved.getStatut().name());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SouscriptionResponseDTO> list() {
        return souscriptionRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SouscriptionResponseDTO findById(String id) {
        return souscriptionRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable"));
    }

    @Transactional
    public void delete(String id, String actorUserId) {
        if (!souscriptionRepository.existsById(id)) {
            throw new EntityNotFoundException("Souscription introuvable");
        }
        souscriptionRepository.deleteById(id);
        auditService.log(actorUserId, "SOUSCRIPTION_DELETE", "SOUSCRIPTION", id, "deleted");
    }

    private SouscriptionResponseDTO toDto(Souscription s) {
        return new SouscriptionResponseDTO(
                s.getId(),
                s.getClient().getId(),
                s.getAgent() != null ? s.getAgent().getId() : null,
                s.getPack().getId(),
                s.getDateDebut(),
                s.getDateFin(),
                s.getDateProchainPaiement(),
                s.getStatut(),
                s.getCreatedAt()
        );
    }
}
