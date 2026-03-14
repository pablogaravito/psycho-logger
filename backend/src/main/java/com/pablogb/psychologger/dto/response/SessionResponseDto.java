package com.pablogb.psychologger.dto.response;

import com.pablogb.psychologger.model.enums.SessionStatus;
import com.pablogb.psychologger.model.enums.SessionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponseDto {

    private Integer id;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private String mainThemes;
    private String content;
    private String nextSession;
    private Boolean isRelevant;
    private SessionType sessionType;
    private SessionStatus status;
    private Set<PatientSummaryDto> patients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
