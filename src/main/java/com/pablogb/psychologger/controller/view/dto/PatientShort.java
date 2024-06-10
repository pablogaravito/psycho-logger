package com.pablogb.psychologger.controller.view.dto;

import com.pablogb.psychologger.domain.entity.PatientEntity;
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
}
