package com.pablogb.psychologger.domain.dao;

import com.pablogb.psychologger.domain.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchPatientDto {
    private Long id;
    private String firstNames;
    private String lastNames;
    private String shortName;
    private LocalDate birthDate;

    private Boolean isActive;
    private Sex sex;
    private Set<SessionDto> sessions;
}
