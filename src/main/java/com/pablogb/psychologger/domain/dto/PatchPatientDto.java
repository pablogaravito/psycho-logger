package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.domain.entity.Sex;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
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
