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
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDto {

    private Long id;

    @NotBlank(message = "First Name cannot be blank")
    private String firstNames;
    @NotBlank(message = "Last Name cannot be blank")
    private String lastNames;
    private String shortName;
    private LocalDate birthDate;
    private Boolean isActive;
    private Sex sex;
    private Set<SessionDto> sessions;
}
