package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.response.AuditLogResponseDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface AuditLogService {
    Page<AuditLogResponseDto> getLogs(
            String entityType,
            Integer userId,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size);
}
