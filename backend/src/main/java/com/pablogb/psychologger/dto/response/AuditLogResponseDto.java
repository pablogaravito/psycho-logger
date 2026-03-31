package com.pablogb.psychologger.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponseDto {
    private Integer id;
    private String userName;
    private String action;
    private String entityType;
    private Integer entityId;
    private String details;
    private LocalDateTime createdAt;
}
