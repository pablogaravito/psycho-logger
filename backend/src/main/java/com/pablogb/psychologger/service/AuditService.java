package com.pablogb.psychologger.service;

import com.pablogb.psychologger.model.enums.AuditAction;

public interface AuditService {
    void log(AuditAction action, String entityType, Integer entityId);
    void log(AuditAction action, String entityType, Integer entityId, String details);
}
