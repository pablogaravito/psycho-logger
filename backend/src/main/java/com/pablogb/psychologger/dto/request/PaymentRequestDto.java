package com.pablogb.psychologger.dto.request;

import com.pablogb.psychologger.model.enums.PaymentMethod;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    @NotNull(message = "Patient is required")
    private Integer patientId;

    private Integer sessionId;
    private Integer paymentPlanId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String currency = "SOL";
    private PaymentStatus status = PaymentStatus.PENDING;
    private PaymentMethod paymentMethod;
    private LocalDateTime paidAt;
    private String notes;
}
