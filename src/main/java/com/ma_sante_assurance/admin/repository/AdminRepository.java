package com.ma_sante_assurance.admin.repository;

import com.ma_sante_assurance.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {
}
