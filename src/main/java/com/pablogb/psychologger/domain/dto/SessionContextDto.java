package com.pablogb.psychologger.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionContextDto {

    @NonNull
    private List<Long> patientId;
    @NonNull
    private SessionDto sessionDto;
}
