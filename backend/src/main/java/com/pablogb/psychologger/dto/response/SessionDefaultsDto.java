package com.pablogb.psychologger.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDefaultsDto {
    private String defaultCurrency;
    private Integer defaultSessionDuration;
    private BigDecimal defaultSessionPrice;
}
