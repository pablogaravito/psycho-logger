package com.pablogb.psychologger.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientWithBirthdayContextDto {

    private Long id;
    private String shortName;
    private String birthDate;
}
