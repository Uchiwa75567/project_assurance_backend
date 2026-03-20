package com.ma_sante_assurance.paiement.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.paiement.dto.IPNRequest;
import com.ma_sante_assurance.paiement.dto.PaiementRequestDTO;
import com.ma_sante_assurance.paiement.dto.PaiementResponseDTO;
import com.ma_sante_assurance.paiement.dto.PaydunyaInitRequest;
import com.ma_sante_assurance.paiement.dto.PaydunyaInitResponse;
import com.ma_sante_assurance.paiement.service.PaiementService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/paiements")
@Tag(name = "Paiements", description = "Gestion des paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @GetMapping
    @Operation(summary = "Lister les paiements")
    public ApiResponse<List<PaiementResponseDTO>> list() {
        return ApiResponse.ok("Paiements recuperes", paiementService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details paiement")
    public ApiResponse<PaiementResponseDTO> get(@Parameter(description = "ID paiement") @PathVariable String id) {
        return ApiResponse.ok("Paiement recupere", paiementService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer un paiement")
    public ApiResponse<PaiementResponseDTO> create(@Valid @RequestBody PaiementRequestDTO.Create request,
                                                    Authentication authentication) {
        return ApiResponse.ok("Paiement cree", paiementService.create(request, authentication.getName()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour un paiement")
    public ApiResponse<PaiementResponseDTO> update(@Parameter(description = "ID paiement") @PathVariable String id,
                                                    @RequestBody PaiementRequestDTO.Update request,
                                                    Authentication authentication) {
        return ApiResponse.ok("Paiement mis a jour", paiementService.update(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un paiement")
    public ApiResponse<Void> delete(@Parameter(description = "ID paiement") @PathVariable String id) {
        paiementService.delete(id);
        return ApiResponse.ok("Paiement supprime", null);
    }

    @PostMapping("/initier")
    @Operation(summary = "Initier paiement PayDunya", description = "Crée souscription + paiement + retourne URL PayDunya")
    public ApiResponse<PaydunyaInitResponse> initier(
            @Valid @RequestBody PaydunyaInitRequest request,
            Authentication authentication
    ) {
        log.info("Init PayDunya: client={}, pack={}", request.clientId(), request.packId());
        return ApiResponse.ok("Paiement initié - redirection PayDunya", 
                paiementService.initierPaiement(request, authentication.getName()));
    }

    @PostMapping("/paydunya/ipn")
    @Operation(summary = "IPN PayDunya", description = "Webhook notification paiement (HTTP 200 toujours)")
    public void ipn(@RequestBody IPNRequest request) {
        log.info("IPN PayDunya reçu: {}", request);
        paiementService.handleIPN(request);
        // Toujours 200 pour PayDunya (ne jamais reject webhook)
    }
}

