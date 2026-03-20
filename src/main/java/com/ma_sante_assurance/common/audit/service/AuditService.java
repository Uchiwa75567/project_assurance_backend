package com.ma_sante_assurance.common.audit.service;

import com.ma_sante_assurance.common.audit.entity.AuditLog;
import com.ma_sante_assurance.common.audit.repository.AuditLogRepository;
import com.ma_sante_assurance.common.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(String actorUserId, String action, String entityType, String entityId, String details) {
        AuditLog log = AuditLog.builder()
                .id(IdGenerator.uuid())
                .actorUserId(actorUserId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}
