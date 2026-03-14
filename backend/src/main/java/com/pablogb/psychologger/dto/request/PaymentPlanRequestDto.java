package com.pablogb.psychologger.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentPlanRequestDto {

    @NotNull(message = "Patient is required")
    private Integer patientId;

    @NotNull(message = "Total sessions is required")
    private Integer totalSessions;

    private BigDecimal pricePerSession;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    private LocalDateTime paidAt;
}
