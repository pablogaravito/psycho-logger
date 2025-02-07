package com.pablogb.psychologger.dto.view;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.util.DateUtils;
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

//    public static PatientListView create(PatientEntity patientEntity) {
//        return PatientListView.builder()
//                .id(patientEntity.getId())
//                .sex(patientEntity.getSex().getCode())
//                .birthDate(DateUtils.formatShortDate(patientEntity.getBirthDate()))
//                .shortName(patientEntity.getShortName())
//                .build();
//    }
    public static PatientListView createFromDto(PatientDto patientDto) {
        return PatientListView.builder()
                .id(patientDto.getId())
                .sex(patientDto.getSex().getCode())
                .birthDate(DateUtils.formatShortDate(patientDto.getBirthDate()))
                .shortName(patientDto.getShortName())
                .build();
    }
}
