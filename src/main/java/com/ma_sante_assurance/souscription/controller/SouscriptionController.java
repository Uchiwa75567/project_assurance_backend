package com.ma_sante_assurance.souscription.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.souscription.dto.SouscriptionRequestDTO;
import com.ma_sante_assurance.souscription.dto.SouscriptionResponseDTO;
import com.ma_sante_assurance.souscription.service.SouscriptionService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/souscriptions")
@Tag(name = "Souscriptions", description = "Gestion des souscriptions")
public class SouscriptionController {

    private final SouscriptionService souscriptionService;

    public SouscriptionController(SouscriptionService souscriptionService) {
        this.souscriptionService = souscriptionService;
    }

    @GetMapping
    @Operation(summary = "Lister les souscriptions")
    public ApiResponse<List<SouscriptionResponseDTO>> list() {
        return ApiResponse.ok("Souscriptions recuperees", souscriptionService.list());
    }

    @PostMapping
    @Operation(summary = "Creer une souscription")
    public ApiResponse<SouscriptionResponseDTO> create(@Valid @RequestBody SouscriptionRequestDTO.Create request,
                                                        Authentication authentication) {
        return ApiResponse.ok("Souscription creee", souscriptionService.create(request, authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details souscription")
    public ApiResponse<SouscriptionResponseDTO> get(@Parameter(description = "ID souscription") @PathVariable String id) {
        return ApiResponse.ok("Souscription recuperee", souscriptionService.findById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour une souscription")
    public ApiResponse<SouscriptionResponseDTO> update(@Parameter(description = "ID souscription") @PathVariable String id,
                                                        @RequestBody SouscriptionRequestDTO.Update request,
                                                        Authentication authentication) {
        return ApiResponse.ok("Souscription mise a jour", souscriptionService.update(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une souscription")
    public ApiResponse<Void> delete(@Parameter(description = "ID souscription") @PathVariable String id, Authentication authentication) {
        souscriptionService.delete(id, authentication.getName());
        return ApiResponse.ok("Souscription supprimee", null);
    }
}
