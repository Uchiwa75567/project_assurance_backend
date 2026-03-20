package com.ma_sante_assurance.agent.repository;

import com.ma_sante_assurance.agent.entity.AgentLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentLocationRepository extends JpaRepository<AgentLocation, String> {
    Optional<AgentLocation> findTopByAgentIdOrderByUpdatedAtDesc(String agentId);
    List<AgentLocation> findByAgentId(String agentId);
}
