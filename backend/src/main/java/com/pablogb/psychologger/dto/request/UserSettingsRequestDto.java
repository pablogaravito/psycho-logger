package com.pablogb.psychologger.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettingsRequestDto {
    private Integer defaultSessionDuration;
    private BigDecimal defaultSessionPrice;
}
