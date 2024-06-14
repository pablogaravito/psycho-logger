package com.pablogb.psychologger.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientWithDebtContextDto {

    @NonNull
    private Long id;
    @NonNull
    private String shortName;
    @NonNull
    private Long debtCount;
}
