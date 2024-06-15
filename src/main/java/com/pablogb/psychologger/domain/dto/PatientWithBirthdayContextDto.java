package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.utils.DateUtils;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientWithBirthdayContextDto {

    private Long id;
    private String shortName;
    private String birthDate;

    public static PatientWithBirthdayContextDto create(PatientEntity patientEntity) {
        return PatientWithBirthdayContextDto.builder()
                .id(patientEntity.getId())
                .shortName(patientEntity.getShortName())
                .birthDate(DateUtils.formatShortBirthdayDate(patientEntity.getBirthDate()))
                .build();
    }

    @Override
    public String toString() {
        return "PatientWithBirthdayContextDto{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }
}
