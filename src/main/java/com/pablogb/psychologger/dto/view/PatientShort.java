package com.pablogb.psychologger.dto.view;

import com.pablogb.psychologger.dto.api.PatientDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PatientShort {
    private Long id;
    private String shortName;

    public static PatientShort createFromDto(PatientDto patientDto) {
        return new PatientShort(patientDto.getId(), patientDto.getShortName());
    }
}
