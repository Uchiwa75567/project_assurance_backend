package com.ma_sante_assurance.garantie.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.garantie.dto.GarantieRequestDTO;
import com.ma_sante_assurance.garantie.dto.GarantieResponseDTO;
import com.ma_sante_assurance.garantie.service.GarantieService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/garanties")
@Tag(name = "Garanties", description = "Gestion des garanties")
public class GarantieController {

    private final GarantieService garantieService;

    public GarantieController(GarantieService garantieService) {
        this.garantieService = garantieService;
    }

    @GetMapping
    @Operation(summary = "Lister les garanties")
    public ApiResponse<List<GarantieResponseDTO>> list() {
        return ApiResponse.ok("Garanties recuperees", garantieService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details garantie")
    public ApiResponse<GarantieResponseDTO> get(@Parameter(description = "ID garantie") @PathVariable String id) {
        return ApiResponse.ok("Garantie recuperee", garantieService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer une garantie")
    public ApiResponse<GarantieResponseDTO> create(@Valid @RequestBody GarantieRequestDTO request) {
        return ApiResponse.ok("Garantie creee", garantieService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour une garantie")
    public ApiResponse<GarantieResponseDTO> update(@Parameter(description = "ID garantie") @PathVariable String id,
                                                   @Valid @RequestBody GarantieRequestDTO request) {
        return ApiResponse.ok("Garantie mise a jour", garantieService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une garantie")
    public ApiResponse<Void> delete(@Parameter(description = "ID garantie") @PathVariable String id) {
        garantieService.delete(id);
        return ApiResponse.ok("Garantie supprimee", null);
    }
}
