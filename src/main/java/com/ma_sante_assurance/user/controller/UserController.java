package com.ma_sante_assurance.user.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.user.dto.UserRequestDTO;
import com.ma_sante_assurance.user.dto.UserResponseDTO;
import com.ma_sante_assurance.user.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Gestion des utilisateurs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Lister les utilisateurs")
    public ApiResponse<List<UserResponseDTO>> listUsers() {
        return ApiResponse.ok("Users recuperes", userService.list());
    }

    @PostMapping
    @Operation(summary = "Creer un utilisateur")
    public ApiResponse<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO request) {
        return ApiResponse.ok("User cree", userService.create(request));
    }
}
