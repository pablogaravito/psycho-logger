package com.pablogb.psychologger.dto.request;

import com.pablogb.psychologger.model.enums.SessionStatus;
import com.pablogb.psychologger.model.enums.SessionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionRequestDto {

    @NotNull(message = "Scheduled date and time is required")
    private LocalDateTime scheduledAt;

    private Integer durationMinutes = 50;
    private String mainThemes;
    private String content;
    private String nextSession;
    private Boolean isRelevant = false;
    private SessionType sessionType = SessionType.INDIVIDUAL;
    private SessionStatus status = SessionStatus.SCHEDULED;

    @NotNull(message = "At least one patient is required")
    private Set<Integer> patientIds;
}