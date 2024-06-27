package com.pablogb.psychologger.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DebtSessionShortDto {
    private Long id;
    private Boolean isPaid;
}
