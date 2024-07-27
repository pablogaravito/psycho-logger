package com.pablogb.psychologger.dto.api;

import com.pablogb.psychologger.model.entity.SessionEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {

    private Long id;
    private LocalDate sessionDate;
    private String themes;
    private String content;
    private Boolean isImportant;
    private Boolean isPaid;
    private String nextWeek;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<PatientDto> patients;

    public static SessionDto create(SessionEntity sessionEntity) {
        return SessionDto.builder()
                .id(sessionEntity.getId())
                .sessionDate(sessionEntity.getSessionDate())
                .themes(sessionEntity.getThemes())
                .content(sessionEntity.getContent())
                .isImportant(sessionEntity.getIsImportant())
                .isPaid(sessionEntity.getIsPaid())
                .nextWeek(sessionEntity.getNextWeek())
                .patients(sessionEntity.getPatients().stream().map(PatientDto::create).toList())
                .build();
    }
}
