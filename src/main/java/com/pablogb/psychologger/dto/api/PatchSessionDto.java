package com.pablogb.psychologger.dto.api;

import com.pablogb.psychologger.model.entity.PatientEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchSessionDto {
    private Long id;
    private LocalDate sessionDate;
    private String themes;
    private String content;
    private Boolean isImportant;
    private Boolean isPaid;
    private String nextWeek;
    private List<PatientEntity> patients;
}
