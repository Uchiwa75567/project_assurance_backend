package com.ma_sante_assurance.carte.controller;

import com.ma_sante_assurance.carte.dto.CarteRequestDTO;
import com.ma_sante_assurance.carte.dto.CarteResponseDTO;
import com.ma_sante_assurance.carte.service.CarteService;
import com.ma_sante_assurance.common.ApiResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cartes")
@Tag(name = "Cartes", description = "Cartes d'assurance")
public class CarteController {

    private final CarteService carteService;

    public CarteController(CarteService carteService) {
        this.carteService = carteService;
    }

    @GetMapping("/souscription/{souscriptionId}")
    @Operation(summary = "Carte par souscription")
    public ApiResponse<CarteResponseDTO> getBySouscription(@Parameter(description = "ID souscription") @PathVariable String souscriptionId) {
        return ApiResponse.ok("Carte recuperee", carteService.getBySouscription(souscriptionId));
    }

    @PostMapping
    @Operation(summary = "Creer ou mettre a jour une carte")
    public ApiResponse<CarteResponseDTO> createOrUpdate(@Valid @RequestBody CarteRequestDTO request) {
        return ApiResponse.ok("Carte enregistree", carteService.createOrUpdate(request));
    }
}
