package com.pablogb.psychologger.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class DebtSessionDto {
    private Long id;
    private String themes;
    private String sessionDate;
    private boolean isPaid;

    public DebtSessionDto(Long id, String themes, LocalDate sessionDate, boolean isPaid) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        this.id = id;
        this.themes = themes;
        this.sessionDate = sessionDate.format(format);
        this.isPaid = isPaid;
    }
}
