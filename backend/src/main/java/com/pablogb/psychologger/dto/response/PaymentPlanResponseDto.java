package com.pablogb.psychologger.dto.response;

import com.pablogb.psychologger.model.enums.PaymentPlanStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentPlanResponseDto {

    private Integer id;
    private Integer patientId;
    private String patientName;
    private Integer totalSessions;
    private Integer sessionsUsed;
    private BigDecimal pricePerSession;
    private BigDecimal totalAmount;
    private PaymentPlanStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
