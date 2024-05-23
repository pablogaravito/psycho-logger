package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {
    private Long id;

    @NonNull
    private LocalDate sessionDate;

    @NotBlank(message = "Subject cannot be blank")
    @NonNull
    private String subject;

    @NotBlank(message = "Session's content cannot be blank")
    @NonNull
    private String content;

    @NonNull
    @Builder.Default
    private Boolean isImportant = false;

    @NonNull
    @Builder.Default
    private Boolean isPaid = false;

    private String nextWeek;

    @NonNull
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sessions")
    private Set<PatientEntity> patients;
}
