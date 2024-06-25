package com.pablogb.psychologger.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientWithDebtContextDto {

    @NonNull
    private Long id;
    @NonNull
    private String shortName;
    @NonNull
    private Long debtCount;
    @NonNull
    private List<DebtSessionDto> debtSessions;

    public static PatientWithDebtContextDto create(PatientWithDebtCountDto patientWithDebtCountDto, List<DebtSessionDto> debtSessions) {
        return PatientWithDebtContextDto.builder()
                .id(patientWithDebtCountDto.getId())
                .shortName(patientWithDebtCountDto.getShortName())
                .debtCount(patientWithDebtCountDto.getDebtCount())
                .debtSessions(debtSessions)
                .build();
    }
}
