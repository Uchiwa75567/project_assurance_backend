package com.ma_sante_assurance.conventionpartenaire.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.conventionpartenaire.dto.ConventionPartenaireRequestDTO;
import com.ma_sante_assurance.conventionpartenaire.dto.ConventionPartenaireResponseDTO;
import com.ma_sante_assurance.conventionpartenaire.service.ConventionPartenaireService;
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
@RequestMapping("/api/conventions-partenaires")
@Tag(name = "ConventionsPartenaires", description = "Conventions entre packs et partenaires")
public class ConventionPartenaireController {

    private final ConventionPartenaireService conventionService;

    public ConventionPartenaireController(ConventionPartenaireService conventionService) {
        this.conventionService = conventionService;
    }

    @GetMapping
    @Operation(summary = "Lister les conventions", description = "Filtrer par packId ou partenaireId.")
    public ApiResponse<List<ConventionPartenaireResponseDTO>> list(
            @Parameter(description = "ID pack") @RequestParam(required = false) String packId,
            @Parameter(description = "ID partenaire") @RequestParam(required = false) String partenaireId
    ) {
        if (packId != null && !packId.isBlank()) {
            return ApiResponse.ok("Conventions recuperees", conventionService.listByPack(packId));
        }
        if (partenaireId != null && !partenaireId.isBlank()) {
            return ApiResponse.ok("Conventions recuperees", conventionService.listByPartenaire(partenaireId));
        }
        return ApiResponse.ok("Conventions recuperees", List.of());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details convention")
    public ApiResponse<ConventionPartenaireResponseDTO> get(@Parameter(description = "ID convention") @PathVariable String id) {
        return ApiResponse.ok("Convention recuperee", conventionService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer une convention")
    public ApiResponse<ConventionPartenaireResponseDTO> create(@Valid @RequestBody ConventionPartenaireRequestDTO request) {
        return ApiResponse.ok("Convention creee", conventionService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour une convention")
    public ApiResponse<ConventionPartenaireResponseDTO> update(@Parameter(description = "ID convention") @PathVariable String id,
                                                                @RequestBody ConventionPartenaireRequestDTO request) {
        return ApiResponse.ok("Convention mise a jour", conventionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une convention")
    public ApiResponse<Void> delete(@Parameter(description = "ID convention") @PathVariable String id) {
        conventionService.delete(id);
        return ApiResponse.ok("Convention supprimee", null);
    }
}
