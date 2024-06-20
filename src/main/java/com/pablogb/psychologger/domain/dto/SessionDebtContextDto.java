package com.pablogb.psychologger.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDebtContextDto {
   private Long id;
   private String themes;
   private boolean paid;
}
