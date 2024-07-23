package com.pablogb.psychologger.dto.api;

import lombok.*;

import java.time.LocalDate;
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
    private List<PatientDto> patients;

    public static SessionDto create(CreateSessionDto createSessionDto) {
        return SessionDto.builder()
                .sessionDate(createSessionDto.getSessionDate())
                .themes(createSessionDto.getThemes())
                .content(createSessionDto.getContent())
                .isImportant(createSessionDto.getIsImportant())
                .isPaid(createSessionDto.getIsPaid())
                .nextWeek(createSessionDto.getNextWeek())
                .build();
    }
}
