package com.pablogb.psychologger.dto.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientCreationDto {

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
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    @NonNull
    @Builder.Default
    private Boolean isActive = true;
    @NonNull
    private Character sex;
}
