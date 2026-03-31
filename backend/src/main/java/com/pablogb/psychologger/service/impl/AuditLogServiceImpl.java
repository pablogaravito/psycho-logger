package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.response.AuditLogResponseDto;
import com.pablogb.psychologger.repository.AuditLogRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getLogs(
            String entityType,
            Integer userId,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size) {

        Integer orgId = securityUtils.getCurrentOrgId();
        PageRequest pageable = PageRequest.of(page, size);

        return auditLogRepository.findFiltered(
                        orgId, entityType, userId, from, to, pageable)
                .map(a -> AuditLogResponseDto.builder()
                        .id(a.getId())
                        .userName(a.getUser().getFirstName()
                                + " " + a.getUser().getLastName())
                        .action(a.getAction())
                        .entityType(a.getEntityType())
                        .entityId(a.getEntityId())
                        .details(a.getDetails())
                        .createdAt(a.getCreatedAt())
                        .build());
    }
}
