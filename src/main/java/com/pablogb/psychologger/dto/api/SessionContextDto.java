package com.pablogb.psychologger.dto.api;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionContextDto {

    @NonNull
    private List<Long> patientIds;
    @NonNull
    private CreateSessionDto createSessionDto;
}
