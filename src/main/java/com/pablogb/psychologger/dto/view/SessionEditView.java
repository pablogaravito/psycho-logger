package com.pablogb.psychologger.dto.view;

import com.pablogb.psychologger.dto.api.SessionDto;
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

    public static SessionEditView createFromDto(SessionDto sessionDto) {
        return SessionEditView.builder()
                .id(sessionDto.getId())
                .sessionDate(DateUtils.formatShortDate(sessionDto.getSessionDate()))
                .themes(sessionDto.getThemes())
                .content(sessionDto.getContent())
                .isImportant(sessionDto.getIsImportant())
                .isPaid(sessionDto.getIsPaid())
                .nextWeek(sessionDto.getNextWeek())
                .build();
    }
}
