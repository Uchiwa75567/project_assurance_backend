package com.ma_sante_assurance.client.service;

import com.ma_sante_assurance.client.dto.ClientRequestDTO;
import com.ma_sante_assurance.client.dto.ClientResponseDTO;
import com.ma_sante_assurance.client.entity.Client;
import com.ma_sante_assurance.client.mapper.ClientMapper;
import com.ma_sante_assurance.client.repository.ClientRepository;
import com.ma_sante_assurance.common.audit.service.AuditService;
import com.ma_sante_assurance.common.dto.PageResponse;
import com.ma_sante_assurance.common.enums.GeneralStatus;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.common.util.NameParser;
import com.ma_sante_assurance.common.util.NameParts;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AuditService auditService;
    private final NumeroAssuranceGenerator numeroAssuranceGenerator;

    public ClientService(ClientRepository clientRepository,
                         ClientMapper clientMapper,
                         AuditService auditService,
                         NumeroAssuranceGenerator numeroAssuranceGenerator) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.auditService = auditService;
        this.numeroAssuranceGenerator = numeroAssuranceGenerator;
    }

    @Transactional
    public ClientResponseDTO create(ClientRequestDTO.Create request) {
        Client client = Client.builder()
                .id(IdGenerator.uuid())
                .userId(request.userId())
                .numeroAssurance(numeroAssuranceGenerator.generate())
                .prenom(request.prenom())
                .nom(request.nom())
                .dateNaissance(request.dateNaissance())
                .telephone(request.telephone())
                .adresse(request.adresse())
                .numeroCni(request.numeroCni())
                .photoUrl(request.photoUrl())
                .typeAssurance(request.typeAssurance())
                .statut(request.statut() == null ? GeneralStatus.ACTIVE : request.statut())
                .createdByAgentId(request.createdByAgentId())
                .build();

        Client saved = clientRepository.save(client);
        auditService.log(request.createdByAgentId(), "CLIENT_CREATE", "CLIENT", saved.getId(), saved.getNumeroAssurance());
        return clientMapper.toDto(saved);
    }

    @Transactional
    public ClientResponseDTO createFromUser(ClientRequestDTO.CreateFromUser request) {
        NameParts parts = NameParser.split(request.fullName());

        Client client = Client.builder()
                .id(IdGenerator.uuid())
                .userId(request.userId())
                .numeroAssurance(numeroAssuranceGenerator.generate())
                .prenom(parts.prenom())
                .nom(parts.nom().isBlank() ? "Client" : parts.nom())
                .dateNaissance(request.dateNaissance())
                .telephone(request.telephone() == null ? "N/A" : request.telephone())
                .numeroCni(request.numeroCni())
                .photoUrl(request.photoUrl())
                .statut(GeneralStatus.ACTIVE)
                .build();

        Client saved = clientRepository.save(client);
        auditService.log(request.userId(), "CLIENT_CREATE", "CLIENT", saved.getId(), saved.getNumeroAssurance());
        return clientMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ClientResponseDTO> list(String search, GeneralStatus statut, int page, int size, String sortBy, String direction) {
        Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Client> result = clientRepository.search(search, statut, PageRequest.of(page, size, sort));

        return PageResponse.of(
                result.getContent().stream().map(clientMapper::toDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        );
    }

    @Transactional
    public ClientResponseDTO update(String id, ClientRequestDTO.Update request, String actorUserId) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));

        if (request.prenom() != null) client.setPrenom(request.prenom());
        if (request.nom() != null) client.setNom(request.nom());
        if (request.dateNaissance() != null) client.setDateNaissance(request.dateNaissance());
        if (request.telephone() != null) client.setTelephone(request.telephone());
        if (request.adresse() != null) client.setAdresse(request.adresse());
        if (request.numeroCni() != null) client.setNumeroCni(request.numeroCni());
        if (request.photoUrl() != null) client.setPhotoUrl(request.photoUrl());
        if (request.typeAssurance() != null) client.setTypeAssurance(request.typeAssurance());
        if (request.statut() != null) client.setStatut(request.statut());

        Client saved = clientRepository.save(client);
        auditService.log(actorUserId, "CLIENT_UPDATE", "CLIENT", saved.getId(), saved.getNumeroAssurance());
        return clientMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO findById(String id) {
        return clientMapper.toDto(clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable")));
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO findByUserId(String userId) {
        return clientMapper.toDto(clientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable")));
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO findByNumeroAssurance(String numeroAssurance) {
        return clientMapper.toDto(clientRepository.findByNumeroAssurance(numeroAssurance)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable")));
    }

    @Transactional
    public void delete(String id, String actorUserId) {
        if (!clientRepository.existsById(id)) {
            throw new EntityNotFoundException("Client introuvable");
        }
        clientRepository.deleteById(id);
        auditService.log(actorUserId, "CLIENT_DELETE", "CLIENT", id, "deleted");
    }
}
