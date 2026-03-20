package com.ma_sante_assurance.beneficiaire.controller;

import com.ma_sante_assurance.beneficiaire.dto.BeneficiaireRequestDTO;
import com.ma_sante_assurance.beneficiaire.dto.BeneficiaireResponseDTO;
import com.ma_sante_assurance.beneficiaire.service.BeneficiaireService;
import com.ma_sante_assurance.common.ApiResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaires")
@Tag(name = "Beneficiaires", description = "Beneficiaires d'une souscription")
public class BeneficiaireController {

    private final BeneficiaireService beneficiaireService;

    public BeneficiaireController(BeneficiaireService beneficiaireService) {
        this.beneficiaireService = beneficiaireService;
    }

    @GetMapping
    @Operation(summary = "Lister les beneficiaires", description = "Par souscription.")
    public ApiResponse<List<BeneficiaireResponseDTO>> list(@Parameter(description = "ID souscription") @RequestParam String souscriptionId) {
        return ApiResponse.ok("Beneficiaires recuperes", beneficiaireService.listBySouscription(souscriptionId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details beneficiaire")
    public ApiResponse<BeneficiaireResponseDTO> get(@Parameter(description = "ID beneficiaire") @PathVariable String id) {
        return ApiResponse.ok("Beneficiaire recupere", beneficiaireService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer un beneficiaire")
    public ApiResponse<BeneficiaireResponseDTO> create(@Valid @RequestBody BeneficiaireRequestDTO request) {
        return ApiResponse.ok("Beneficiaire cree", beneficiaireService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour un beneficiaire")
    public ApiResponse<BeneficiaireResponseDTO> update(@Parameter(description = "ID beneficiaire") @PathVariable String id,
                                                        @RequestBody BeneficiaireRequestDTO request) {
        return ApiResponse.ok("Beneficiaire mis a jour", beneficiaireService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un beneficiaire")
    public ApiResponse<Void> delete(@Parameter(description = "ID beneficiaire") @PathVariable String id) {
        beneficiaireService.delete(id);
        return ApiResponse.ok("Beneficiaire supprime", null);
    }
}
