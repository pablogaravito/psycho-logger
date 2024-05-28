package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.domain.entity.Sex;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientView {
    private Long id;

    @NotBlank(message = "First Name cannot be blank")
    @NonNull
    private String firstNames;

    @NotBlank(message = "Last Name cannot be blank")
    @NonNull
    private String lastNames;

    @NotBlank(message = "Short Name cannot be blank")
    @NonNull
    private String shortName;

    @NonNull
    private String birthDate;

    @NonNull
    private Boolean isActive;

    @NonNull
    private String sex;
}
