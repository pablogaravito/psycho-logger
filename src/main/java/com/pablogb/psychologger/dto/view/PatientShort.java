package com.pablogb.psychologger.dto.view;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PatientShort {
    private Long id;
    private String name;

    public static PatientShort create(PatientEntity patientEntity) {
        return new PatientShort(patientEntity.getId(), patientEntity.getShortName());
    }
    public static PatientShort create(PatientDto patientDto) {
        return new PatientShort(patientDto.getId(), patientDto.getShortName());
    }
}
