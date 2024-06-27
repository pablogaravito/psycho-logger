package com.pablogb.psychologger.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DebtUpdateForm {
    private List<DebtSessionShortDto> debtSessions = new ArrayList<>();
}
