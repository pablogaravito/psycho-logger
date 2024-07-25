package com.pablogb.psychologger.dto.view;

import com.pablogb.psychologger.dto.api.SessionWithPatientsDto;
import com.pablogb.psychologger.util.DateUtils;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionEditView {
    private Long id;
    private String sessionDate;
    private String themes;
    private String content;
    private Boolean isImportant;

    private Boolean isPaid;

    private String nextWeek;

    private List<PatientShort> patients;

    public static SessionEditView createFromDto(SessionWithPatientsDto sessionWithPatientsDto) {
        return SessionEditView.builder()
                .id(sessionWithPatientsDto.getId())
                .sessionDate(DateUtils.formatShortDate(sessionWithPatientsDto.getSessionDate()))
                .themes(sessionWithPatientsDto.getThemes())
                .content(sessionWithPatientsDto.getContent())
                .isImportant(sessionWithPatientsDto.getIsImportant())
                .isPaid(sessionWithPatientsDto.getIsPaid())
                .nextWeek(sessionWithPatientsDto.getNextWeek())
                .build();
    }
}
