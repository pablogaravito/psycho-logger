package com.pablogb.psychologger.dto.response;

import lombok.*;

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
}
