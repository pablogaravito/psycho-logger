package com.pablogb.psychologger.domain.dao;

import com.pablogb.psychologger.domain.entity.PatientEntity;
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
