package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.utils.DateUtils;
import lombok.*;

@Getter
@Setter
public class PatientWithBirthdayContextDto {

    private Long id;
    private String shortName;
    private String birthDate;

    public PatientWithBirthdayContextDto(Long id, String shortName, String birthDate) {
        this.id = id;
        this.shortName = shortName;
        this.birthDate = DateUtils.formatBirthdayDate(birthDate);
    }
}
