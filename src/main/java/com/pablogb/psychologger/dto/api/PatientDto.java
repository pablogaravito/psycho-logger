package com.pablogb.psychologger.dto.api;

import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.enums.Sex;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDto {
    private Long id;
    private String firstNames;
    private String lastNames;
    private String shortName;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    private Boolean isActive;
    private Sex sex;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static PatientDto create(PatientEntity patientEntity) {
        return PatientDto.builder()
                .id(patientEntity.getId())
                .firstNames(patientEntity.getFirstNames())
                .lastNames(patientEntity.getLastNames())
                .shortName(patientEntity.getShortName())
                .sex(patientEntity.getSex())
                .birthDate(patientEntity.getBirthDate())
                .createdAt(patientEntity.getCreatedAt())
                .modifiedAt(patientEntity.getModifiedAt())
                .isActive(patientEntity.getIsActive())
                .build();
    }
}
