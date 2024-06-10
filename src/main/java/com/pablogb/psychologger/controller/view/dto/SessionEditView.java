package com.pablogb.psychologger.controller.view.dto;

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
    private String subject;
    private String content;
    private Boolean isImportant;

    private Boolean isPaid;

    private String nextWeek;

    private List<PatientShort> patients;
}
