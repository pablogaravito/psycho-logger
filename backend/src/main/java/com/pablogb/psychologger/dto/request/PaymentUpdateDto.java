package com.pablogb.psychologger.dto.request;

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
public class PaymentUpdateDto {
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime paidAt;
    private String notes;
}
