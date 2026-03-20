package com.ma_sante_assurance.admin.service;

import com.ma_sante_assurance.admin.dto.AdminRequestDTO;
import com.ma_sante_assurance.admin.dto.AdminResponseDTO;
import com.ma_sante_assurance.admin.entity.Admin;
import com.ma_sante_assurance.admin.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public AdminResponseDTO create(AdminRequestDTO request) {
        Admin admin = Admin.builder().id(request.id()).build();
        return toDto(adminRepository.save(admin));
    }

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> list() {
        return adminRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AdminResponseDTO findById(String id) {
        return adminRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Admin introuvable"));
    }

    @Transactional
    public AdminResponseDTO update(String id, AdminRequestDTO request) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin introuvable"));
        if (request.id() != null && !request.id().isBlank()) {
            admin.setId(request.id());
        }
        return toDto(adminRepository.save(admin));
    }

    @Transactional
    public void delete(String id) {
        if (!adminRepository.existsById(id)) {
            throw new EntityNotFoundException("Admin introuvable");
        }
        adminRepository.deleteById(id);
    }

    private AdminResponseDTO toDto(Admin admin) {
        return new AdminResponseDTO(admin.getId());
    }
}
