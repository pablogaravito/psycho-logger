package com.pablogb.psychologger.domain.dto;

import com.pablogb.psychologger.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DebtSessionDto {
    private Long id;
    private String themes;
    private String sessionDate;
    private boolean isPaid;

    public DebtSessionDto(Long id, String themes, LocalDate sessionDate, boolean isPaid) {
        this.id = id;
        this.themes = themes;
        this.sessionDate = DateUtils.formatShortDate(sessionDate);
        this.isPaid = isPaid;
    }
}
