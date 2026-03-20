package com.pablogb.psychologger.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsResponseDto {
    private long activePatients;
    private long sessionsThisMonth;
    private long pendingPayments;
    private long upcomingSessions;
    private BigDecimal collectedThisMonth;
    private long birthdaysThisMonth;
}
