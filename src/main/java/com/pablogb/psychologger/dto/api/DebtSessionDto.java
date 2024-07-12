package com.pablogb.psychologger.dto.api;

import com.pablogb.psychologger.dto.view.DebtSessionViewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DebtSessionDto {
    private Long id;
    private String themes;
    private String sessionDate;

    public static DebtSessionDto create(DebtSessionViewDto debtSessionViewDto) {
        return new DebtSessionDto(debtSessionViewDto.getId(), debtSessionViewDto.getThemes(), debtSessionViewDto.getSessionDate());
    }
}
