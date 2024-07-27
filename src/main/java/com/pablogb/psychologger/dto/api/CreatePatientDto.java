package com.pablogb.psychologger.dto.api;

import com.pablogb.psychologger.model.enums.Sex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePatientDto {

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
}
