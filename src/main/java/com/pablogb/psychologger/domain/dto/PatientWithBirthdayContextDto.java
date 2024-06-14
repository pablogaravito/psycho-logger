package com.pablogb.psychologger.domain.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientWithBirthdayContextDto {
    @NonNull
    private Long id;
    @NonNull
    private String shortName;
    @NonNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    @Override
    public String toString() {
        return "PatientWithBirthdayContextDto{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }
}
