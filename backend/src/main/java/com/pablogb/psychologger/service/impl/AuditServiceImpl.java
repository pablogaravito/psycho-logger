package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.model.entity.AuditLog;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.model.enums.AuditAction;
import com.pablogb.psychologger.repository.AuditLogRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Async
    public void log(AuditAction action, String entityType, Integer entityId) {
        log(action, entityType, entityId, null);
    }

    @Override
    @Async("auditExecutor")
    public void log(AuditAction action, String entityType,
                    Integer entityId, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .organization(securityUtils.getCurrentUser().getOrganization())
                    .user(securityUtils.getCurrentUser())
                    .action(action.name())
                    .entityType(entityType)
                    .entityId(entityId)
                    .details(details)
                    .createdAt(LocalDateTime.now())
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // never let audit logging break the main flow
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void logWithUser(AuditAction action, String entityType,
                            Integer entityId, String details, User user) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .organization(user.getOrganization())
                    .user(user)
                    .action(action.name())
                    .entityType(entityType)
                    .entityId(entityId)
                    .details(details)
                    .createdAt(LocalDateTime.now())
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }
}
