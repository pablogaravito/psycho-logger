package com.pablogb.psychologger.dto.api;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientWithDebtCountDto {
    @NonNull
    private Long id;
    @NonNull
    private String shortName;
    @NonNull
    private Long debtCount;
}
