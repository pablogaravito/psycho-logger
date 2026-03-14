package com.pablogb.psychologger.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSummaryDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private String shortName;
}
