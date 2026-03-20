package com.ma_sante_assurance.packgarantie.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.packgarantie.dto.PackGarantieRequestDTO;
import com.ma_sante_assurance.packgarantie.dto.PackGarantieResponseDTO;
import com.ma_sante_assurance.packgarantie.service.PackGarantieService;
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
@RequestMapping("/api/pack-garanties")
@Tag(name = "PackGaranties", description = "Association Packs <-> Garanties")
public class PackGarantieController {

    private final PackGarantieService packGarantieService;

    public PackGarantieController(PackGarantieService packGarantieService) {
        this.packGarantieService = packGarantieService;
    }

    @GetMapping
    @Operation(summary = "Lister les associations", description = "Filtrer par packId ou garantieId.")
    public ApiResponse<List<PackGarantieResponseDTO>> list(
            @Parameter(description = "Filtre par pack") @RequestParam(required = false) String packId,
            @Parameter(description = "Filtre par garantie") @RequestParam(required = false) String garantieId
    ) {
        if (packId != null && !packId.isBlank()) {
            return ApiResponse.ok("Garanties du pack recuperees", packGarantieService.listByPack(packId));
        }
        if (garantieId != null && !garantieId.isBlank()) {
            return ApiResponse.ok("Packs de la garantie recuperees", packGarantieService.listByGarantie(garantieId));
        }
        return ApiResponse.ok("Associations recuperees", List.of());
    }

    @PostMapping
    @Operation(summary = "Creer une association pack-garantie")
    public ApiResponse<PackGarantieResponseDTO> create(@Valid @RequestBody PackGarantieRequestDTO request) {
        return ApiResponse.ok("Association creee", packGarantieService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour une association")
    public ApiResponse<PackGarantieResponseDTO> update(@Parameter(description = "ID association") @PathVariable String id,
                                                       @RequestBody PackGarantieRequestDTO request) {
        return ApiResponse.ok("Association mise a jour", packGarantieService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une association")
    public ApiResponse<Void> delete(@Parameter(description = "ID association") @PathVariable String id) {
        packGarantieService.delete(id);
        return ApiResponse.ok("Association supprimee", null);
    }
}
