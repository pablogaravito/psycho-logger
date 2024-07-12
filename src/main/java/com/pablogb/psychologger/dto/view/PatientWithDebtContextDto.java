package com.pablogb.psychologger.dto.view;

import com.pablogb.psychologger.dto.api.PatientWithDebtCountDto;
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
    private List<DebtSessionViewDto> debtSessions;

    public static PatientWithDebtContextDto create(PatientWithDebtCountDto patientWithDebtCountDto, List<DebtSessionViewDto> debtSessions) {
        return PatientWithDebtContextDto.builder()
                .id(patientWithDebtCountDto.getId())
                .shortName(patientWithDebtCountDto.getShortName())
                .debtCount(patientWithDebtCountDto.getDebtCount())
                .debtSessions(debtSessions)
                .build();
    }
}
