package com.ma_sante_assurance.agent.controller;

import com.ma_sante_assurance.agent.dto.AgentLiveLocationDTO;
import com.ma_sante_assurance.agent.dto.AgentLocationUpdateRequestDTO;
import com.ma_sante_assurance.agent.dto.AgentRequestDTO;
import com.ma_sante_assurance.agent.dto.AgentResponseDTO;
import com.ma_sante_assurance.agent.service.AgentService;
import com.ma_sante_assurance.common.ApiResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@Tag(name = "Agents", description = "Gestion des agents et tracking")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    @Operation(summary = "Lister les agents")
    public ApiResponse<List<AgentResponseDTO>> getAgents() {
        return ApiResponse.ok("Agents recuperes", agentService.getAgents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details agent")
    public ApiResponse<AgentResponseDTO> getAgent(@Parameter(description = "ID agent") @PathVariable String id) {
        return ApiResponse.ok("Agent recupere", agentService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer ou mettre a jour un agent")
    public ApiResponse<AgentResponseDTO> createOrUpdateAgent(@Valid @RequestBody AgentRequestDTO request) {
        return ApiResponse.ok("Agent enregistre", agentService.createOrUpdateAgent(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un agent")
    public ApiResponse<Void> delete(@Parameter(description = "ID agent") @PathVariable String id) {
        agentService.delete(id);
        return ApiResponse.ok("Agent supprime", null);
    }

    @GetMapping("/live-locations")
    @Operation(summary = "Positions live", description = "Dernieres positions connues des agents")
    public ApiResponse<List<AgentLiveLocationDTO>> getLiveLocations() {
        return ApiResponse.ok("Positions live recuperees", agentService.getLiveLocations());
    }

    @PostMapping("/{agentId}/location")
    @Operation(summary = "Mettre a jour position agent", description = "Enregistre la position GPS courante")
    public ApiResponse<AgentLiveLocationDTO> updateLocation(
            @Parameter(description = "ID agent") @PathVariable String agentId,
                                                             @Valid @RequestBody AgentLocationUpdateRequestDTO request) {
        return ApiResponse.ok("Position mise a jour", agentService.updateAgentLocation(agentId, request));
    }
}
