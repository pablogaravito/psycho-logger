package com.pablogb.psychologger.controller.view;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionView {

    private Long id;

    @NonNull
    private String sessionDate;

    @NotBlank(message = "Subject cannot be blank")
    @NonNull
    private String subject;

    @NotBlank(message = "Session's content cannot be blank")
    @NonNull
    private String content;

    @NonNull
    private Boolean isImportant = false;

    @NonNull
    private Boolean isPaid = false;

    private String nextWeek;

    @NonNull
    private Set<PatientView> patients;
}
