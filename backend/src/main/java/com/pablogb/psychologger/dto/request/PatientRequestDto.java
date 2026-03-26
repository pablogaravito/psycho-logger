package com.pablogb.psychologger.dto.request;

import com.pablogb.psychologger.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;
    private String shortName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String notes;
    private Boolean isActive;
    private BigDecimal defaultPrice;
    private Boolean hasDebtFlag;
    private String debtFlagNote;
    private String handoverNotes;
    private Boolean assignToMe = true;
    private Integer calendarColor;
}
