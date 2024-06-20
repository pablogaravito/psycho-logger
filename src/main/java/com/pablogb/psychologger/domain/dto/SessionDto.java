package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {
    private Long id;

    @NonNull
    private LocalDate sessionDate;

    @NotBlank(message = "Themes cannot be blank")
    @NonNull
    private String themes;

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
    private Set<PatientEntity> patients;
}
