package com.pablogb.psychologger.dto.response;

import com.pablogb.psychologger.model.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private String shortName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Gender gender;
    private Boolean isActive;
    private String notes;
    private LocalDateTime createdAt;
}
