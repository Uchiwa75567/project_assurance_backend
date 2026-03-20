package com.ma_sante_assurance.agent.repository;

import com.ma_sante_assurance.agent.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, String> {
}
