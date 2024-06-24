package com.pablogb.psychologger.controller.gui.view;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.utils.DateUtils;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientListView {
    private Long id;

    private String shortName;

    private String birthDate;

    private Character sex;

    public static PatientListView create(PatientEntity patientEntity) {
        return PatientListView.builder()
                .id(patientEntity.getId())
                .sex(patientEntity.getSex().getCode())
                .birthDate(DateUtils.formatShortDate(patientEntity.getBirthDate()))
                .shortName(patientEntity.getShortName())
                .build();
    }
}
