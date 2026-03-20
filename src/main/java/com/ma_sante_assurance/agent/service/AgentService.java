package com.ma_sante_assurance.agent.service;

import com.ma_sante_assurance.agent.dto.AgentLiveLocationDTO;
import com.ma_sante_assurance.agent.dto.AgentLocationUpdateRequestDTO;
import com.ma_sante_assurance.agent.dto.AgentRequestDTO;
import com.ma_sante_assurance.agent.dto.AgentResponseDTO;
import com.ma_sante_assurance.agent.entity.Agent;
import com.ma_sante_assurance.agent.entity.AgentLocation;
import com.ma_sante_assurance.agent.repository.AgentLocationRepository;
import com.ma_sante_assurance.agent.repository.AgentRepository;
import com.ma_sante_assurance.common.util.NameParser;
import com.ma_sante_assurance.common.util.NameParts;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class AgentService {

    private static final double MOVING_THRESHOLD_KMH = 1.0;

    private final AgentRepository agentRepository;
    private final AgentLocationRepository locationRepository;
    private final AgentLocationBroadcaster broadcaster;

    public AgentService(AgentRepository agentRepository,
                        AgentLocationRepository locationRepository,
                        AgentLocationBroadcaster broadcaster) {
        this.agentRepository = agentRepository;
        this.locationRepository = locationRepository;
        this.broadcaster = broadcaster;
    }

    @Transactional
    public AgentResponseDTO createFromUser(String userId, String fullName) {
        NameParts parts = NameParser.split(fullName);

        Agent agent = Agent.builder()
                .id(userId)
                .matricule("AG-" + String.valueOf(System.currentTimeMillis()).substring(7))
                .prenom(parts.prenom())
                .nom(parts.nom().isBlank() ? "Agent" : parts.nom())
                .telephone("N/A")
                .statut("Active")
                .build();

        return toAgentResponse(agentRepository.save(agent));
    }

    @Transactional
    public AgentResponseDTO createOrUpdateAgent(AgentRequestDTO request) {
        Agent agent = Agent.builder()
                .id(request.id())
                .matricule(request.matricule())
                .prenom(request.prenom())
                .nom(request.nom())
                .telephone(request.telephone())
                .statut(request.statut())
                .build();

        Agent saved = agentRepository.save(agent);
        return toAgentResponse(saved);
    }

    @Transactional
    public AgentLiveLocationDTO updateAgentLocation(String agentId, AgentLocationUpdateRequestDTO request) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new EntityNotFoundException("Agent introuvable: " + agentId));

        AgentLocation location = AgentLocation.builder()
                .id(com.ma_sante_assurance.common.util.IdGenerator.uuid())
                .agentId(agentId)
                .build();

        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setSpeedKmh(request.speedKmh());
        location.setMoving(request.speedKmh() >= MOVING_THRESHOLD_KMH);
        location.setUpdatedAt(Instant.now());

        AgentLocation savedLocation = locationRepository.save(location);

        List<AgentLiveLocationDTO> snapshot = getLiveLocations();
        broadcaster.broadcastSnapshot(snapshot);

        return toLiveDto(agent, savedLocation);
    }

    @Transactional(readOnly = true)
    public List<AgentResponseDTO> getAgents() {
        return agentRepository.findAll().stream()
                .map(this::toAgentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AgentResponseDTO findById(String id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agent introuvable: " + id));
        return toAgentResponse(agent);
    }

    @Transactional
    public void delete(String id) {
        if (!agentRepository.existsById(id)) {
            throw new EntityNotFoundException("Agent introuvable: " + id);
        }
        agentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AgentLiveLocationDTO> getLiveLocations() {
        return locationRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(AgentLocation::getAgentId))
                .values()
                .stream()
                .map(locations -> locations.stream()
                        .max(Comparator.comparing(AgentLocation::getUpdatedAt))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .map(location -> {
                    Agent agent = agentRepository.findById(location.getAgentId())
                            .orElseGet(() -> Agent.builder()
                                    .id(location.getAgentId())
                                    .matricule("N/A")
                                    .prenom("Agent")
                                    .nom(location.getAgentId())
                                    .telephone("N/A")
                                    .statut("Active")
                                    .build());
                    return toLiveDto(agent, location);
                })
                .sorted(Comparator.comparing(AgentLiveLocationDTO::updatedAt).reversed())
                .toList();
    }

    private AgentResponseDTO toAgentResponse(Agent agent) {
        return new AgentResponseDTO(
                agent.getId(),
                agent.getMatricule(),
                agent.getPrenom(),
                agent.getNom(),
                agent.getTelephone(),
                agent.getStatut()
        );
    }

    private AgentLiveLocationDTO toLiveDto(Agent agent, AgentLocation location) {
        return new AgentLiveLocationDTO(
                agent.getId(),
                agent.getMatricule(),
                agent.getPrenom(),
                agent.getNom(),
                agent.getTelephone(),
                agent.getStatut(),
                location.getLatitude(),
                location.getLongitude(),
                location.getSpeedKmh(),
                location.getMoving(),
                location.getUpdatedAt()
        );
    }
}
