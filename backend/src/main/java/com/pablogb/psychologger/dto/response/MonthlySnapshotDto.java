package com.pablogb.psychologger.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySnapshotDto {
    private Integer year;
    private Integer month;
    private Integer activePatients;
    private Integer totalSessions;
    private Integer completedSessions;
    private BigDecimal totalCollected;
    private BigDecimal totalPending;
}