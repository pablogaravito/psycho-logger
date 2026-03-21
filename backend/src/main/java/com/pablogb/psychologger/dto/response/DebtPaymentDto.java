package com.pablogb.psychologger.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtPaymentDto {
    private Integer paymentId;
    private Integer sessionId;
    private LocalDateTime sessionDate;
    private BigDecimal amount;
    private String currency;
}
