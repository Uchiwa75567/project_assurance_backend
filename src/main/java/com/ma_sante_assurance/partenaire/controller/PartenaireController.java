package com.ma_sante_assurance.partenaire.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.partenaire.dto.PartenaireRequestDTO;
import com.ma_sante_assurance.partenaire.dto.PartenaireResponseDTO;
import com.ma_sante_assurance.partenaire.service.PartenaireService;
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
@RequestMapping("/api/partenaires")
@Tag(name = "Partenaires", description = "Gestion des partenaires de sante")
public class PartenaireController {

    private final PartenaireService partenaireService;

    public PartenaireController(PartenaireService partenaireService) {
        this.partenaireService = partenaireService;
    }

    @GetMapping
    @Operation(summary = "Lister les partenaires", description = "Optionnel: calcul de distance via lat/lon.")
    public ApiResponse<List<PartenaireResponseDTO>> list(
            @Parameter(description = "Latitude utilisateur") @RequestParam(required = false) Double lat,
            @Parameter(description = "Longitude utilisateur") @RequestParam(required = false) Double lon
    ) {
        return ApiResponse.ok("Partenaires recuperes", partenaireService.list(lat, lon));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details partenaire")
    public ApiResponse<PartenaireResponseDTO> get(@Parameter(description = "ID partenaire") @PathVariable String id) {
        return ApiResponse.ok("Partenaire recupere", partenaireService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer un partenaire")
    public ApiResponse<PartenaireResponseDTO> create(@Valid @RequestBody PartenaireRequestDTO request) {
        return ApiResponse.ok("Partenaire cree", partenaireService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour un partenaire")
    public ApiResponse<PartenaireResponseDTO> update(@Parameter(description = "ID partenaire") @PathVariable String id,
                                                     @RequestBody PartenaireRequestDTO request) {
        return ApiResponse.ok("Partenaire mis a jour", partenaireService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un partenaire")
    public ApiResponse<Void> delete(@Parameter(description = "ID partenaire") @PathVariable String id) {
        partenaireService.delete(id);
        return ApiResponse.ok("Partenaire supprime", null);
    }
}
