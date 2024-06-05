package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchSessionDto {
    private Long id;
    private LocalDate sessionDate;
    private String subject;
    private String content;
    private Boolean isImportant;
    private Boolean isPaid;
    private String nextWeek;
    private Set<PatientEntity> patients;
}
