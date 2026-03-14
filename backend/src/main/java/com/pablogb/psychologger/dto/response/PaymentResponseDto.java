package com.pablogb.psychologger.dto.response;

import com.pablogb.psychologger.model.enums.PaymentMethod;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private Integer id;
    private Integer patientId;
    private String patientName;
    private Integer sessionId;
    private Integer paymentPlanId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime paidAt;
    private String notes;
    private LocalDateTime createdAt;
}
