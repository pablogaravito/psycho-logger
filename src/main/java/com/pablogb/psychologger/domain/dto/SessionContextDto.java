package com.pablogb.psychologger.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionContextDto {

    @NonNull
    private Long patientId;
    @NonNull
    private SessionDto sessionDto;
}
