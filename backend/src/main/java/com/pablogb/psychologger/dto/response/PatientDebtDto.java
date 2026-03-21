package com.pablogb.psychologger.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDebtDto {
    private Integer patientId;
    private String patientName;
    private String shortName;
    private Boolean hasDebtFlag;
    private Boolean isActive;
    private Integer pendingCount;
    private BigDecimal totalPending;
    private List<DebtPaymentDto> pendingPayments;
}