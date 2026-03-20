package com.ma_sante_assurance.client.controller;

import com.ma_sante_assurance.client.dto.ClientRequestDTO;
import com.ma_sante_assurance.client.dto.ClientResponseDTO;
import com.ma_sante_assurance.client.service.ClientService;
import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.common.dto.PageResponse;
import com.ma_sante_assurance.common.enums.GeneralStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Gestion des clients d'assurance")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Liste des clients", description = "Retourne la liste paginee des clients avec possibilite de filtrage et tri")
    @GetMapping
    public ApiResponse<PageResponse<ClientResponseDTO>> list(
            @Parameter(description = "Recherche par nom, prenom ou numero d'assurance")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filtrer par statut")
            @RequestParam(required = false) GeneralStatus statut,
            @Parameter(description = "Numero de page (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page (max 100)")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Champ de tri")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ApiResponse.ok("Clients recuperes", clientService.list(search, statut, page, size, sortBy, direction));
    }

    @Operation(summary = "Obtenir un client", description = "Retourne les details d'un client par son ID")
    @GetMapping("/{id}")
    public ApiResponse<ClientResponseDTO> getOne(@Parameter(description = "ID du client") @PathVariable String id) {
        return ApiResponse.ok("Client recupere", clientService.findById(id));
    }

    @Operation(summary = "Mon profil client", description = "Retourne le client associe a l'utilisateur connecte")
    @GetMapping("/me")
    public ApiResponse<ClientResponseDTO> me(Authentication authentication) {
        return ApiResponse.ok("Client recupere", clientService.findByUserId(authentication.getName()));
    }

    @Operation(summary = "Creer un client", description = "Cree un nouveau client d'assurance")
    @PostMapping
    public ApiResponse<ClientResponseDTO> create(@Valid @RequestBody ClientRequestDTO.Create request) {
        return ApiResponse.ok("Client cree", clientService.create(request));
    }

    @Operation(summary = "Mettre a jour un client", description = "Met a jour les informations d'un client existant")
    @PatchMapping("/{id}")
    public ApiResponse<ClientResponseDTO> update(@Parameter(description = "ID du client") @PathVariable String id,
                                                  @RequestBody ClientRequestDTO.Update request,
                                                  Authentication authentication) {
        return ApiResponse.ok("Client mis a jour", clientService.update(id, request, authentication.getName()));
    }

    @Operation(summary = "Supprimer un client", description = "Supprime un client par son ID")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "ID du client") @PathVariable String id,
                                    Authentication authentication) {
        clientService.delete(id, authentication.getName());
        return ApiResponse.ok("Client supprime", null);
    }
}
