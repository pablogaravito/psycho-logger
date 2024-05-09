package com.pablogb.psychologger.domain.dao;

import com.pablogb.psychologger.domain.entity.Sex;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Data
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
}
