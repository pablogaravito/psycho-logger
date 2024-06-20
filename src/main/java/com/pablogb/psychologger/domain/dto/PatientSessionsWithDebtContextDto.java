package com.pablogb.psychologger.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientSessionsWithDebtContextDto {
    private Long patientId;
    private String shortName;
    private List<SessionDebtContextDto> sessionsWithDebt;
}
