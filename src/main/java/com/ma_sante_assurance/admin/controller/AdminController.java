package com.ma_sante_assurance.admin.controller;

import com.ma_sante_assurance.admin.dto.AdminRequestDTO;
import com.ma_sante_assurance.admin.dto.AdminResponseDTO;
import com.ma_sante_assurance.admin.service.AdminService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@Tag(name = "Admins", description = "Gestion des admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @Operation(summary = "Lister les admins")
    public ApiResponse<List<AdminResponseDTO>> list() {
        return ApiResponse.ok("Admins recuperes", adminService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Details admin")
    public ApiResponse<AdminResponseDTO> get(@Parameter(description = "ID admin") @PathVariable String id) {
        return ApiResponse.ok("Admin recupere", adminService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer un admin")
    public ApiResponse<AdminResponseDTO> create(@Valid @RequestBody AdminRequestDTO request) {
        return ApiResponse.ok("Admin cree", adminService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour un admin")
    public ApiResponse<AdminResponseDTO> update(@Parameter(description = "ID admin") @PathVariable String id,
                                                 @RequestBody AdminRequestDTO request) {
        return ApiResponse.ok("Admin mis a jour", adminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un admin")
    public ApiResponse<Void> delete(@Parameter(description = "ID admin") @PathVariable String id) {
        adminService.delete(id);
        return ApiResponse.ok("Admin supprime", null);
    }
}
