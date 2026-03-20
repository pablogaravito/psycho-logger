package com.pablogb.psychologger.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponseDto {
    private Integer id;
    private Integer therapistId;
    private Integer patientId;
    private LocalDateTime assignedAt;
    private LocalDateTime unassignedAt;
}