package com.pablogb.psychologger.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BirthdayPatientDto {
    private Integer patientId;
    private String patientName;
    private String shortName;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private Integer age;
    private Integer daysUntil; // negative = past, 0 = today, positive = future
}
