package com.pablogb.psychologger.domain.dao;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionContextDto {

    @NonNull
    private Long patientId;
    @NonNull
    private SessionDto sessionDto;
}
