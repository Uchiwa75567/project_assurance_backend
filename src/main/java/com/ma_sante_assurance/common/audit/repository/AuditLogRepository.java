package com.ma_sante_assurance.common.audit.repository;

import com.ma_sante_assurance.common.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
}
