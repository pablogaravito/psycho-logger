package com.pablogb.psychologger.dto.api;

import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.enums.Sex;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    private LocalDate birthDate;
    private Boolean isActive;
    private Sex sex;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
//    private List<SessionDto> sessions;

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
