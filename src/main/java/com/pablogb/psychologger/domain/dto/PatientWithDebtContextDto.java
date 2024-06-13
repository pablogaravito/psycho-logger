package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientWithDebtContextDto {

    @NonNull
    private Long patientId;
    @NonNull
    private String patientShortName;
    @NonNull
    private Long debtCount;

    @Override
    public String toString() {
        return "PatientWithDebtContextDto{" +
                "patientId=" + patientId +
                ", patientShortName='" + patientShortName + '\'' +
                ", debtCount=" + debtCount +
                '}';
    }
}
