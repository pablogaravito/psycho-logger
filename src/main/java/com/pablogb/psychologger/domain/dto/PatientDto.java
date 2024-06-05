package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.domain.entity.Sex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDto {

    private Long id;

    @NotBlank(message = "First Name cannot be blank")
    @NonNull
    private String firstNames;
    @NotBlank(message = "Last Name cannot be blank")
    @NonNull
    private String lastNames;

    @NotBlank(message = "short Name cannot be blank")
    @NonNull
    private String shortName;

    @Past
    @NonNull
    private LocalDate birthDate;

    @NonNull
    @Builder.Default
    private Boolean isActive = true;

    @NonNull
    private Sex sex;

    private Set<SessionDto> sessions;
}
