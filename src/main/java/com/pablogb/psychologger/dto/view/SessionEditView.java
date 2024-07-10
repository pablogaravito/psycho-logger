package com.pablogb.psychologger.dto.view;

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
}
