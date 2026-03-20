package com.ma_sante_assurance.pack.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.pack.dto.PackRequestDTO;
import com.ma_sante_assurance.pack.dto.PackResponseDTO;
import com.ma_sante_assurance.pack.service.PackService;
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
@RequestMapping("/api/packs")
@Tag(name = "Packs", description = "Gestion des formules d'assurance")
public class PackController {

    private final PackService packService;

    public PackController(PackService packService) {
        this.packService = packService;
    }

    @GetMapping
    @Operation(summary = "Lister les packs")
    public ApiResponse<List<PackResponseDTO>> list() {
        return ApiResponse.ok("Packs recuperes", packService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details d'un pack")
    public ApiResponse<PackResponseDTO> get(@Parameter(description = "ID pack") @PathVariable String id) {
        return ApiResponse.ok("Pack recupere", packService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer un pack")
    public ApiResponse<PackResponseDTO> create(@Valid @RequestBody PackRequestDTO request) {
        return ApiResponse.ok("Pack cree", packService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour un pack")
    public ApiResponse<PackResponseDTO> update(@Parameter(description = "ID pack") @PathVariable String id,
                                               @RequestBody PackRequestDTO request) {
        return ApiResponse.ok("Pack mis a jour", packService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un pack")
    public ApiResponse<Void> delete(@Parameter(description = "ID pack") @PathVariable String id) {
        packService.delete(id);
        return ApiResponse.ok("Pack supprime", null);
    }
}
