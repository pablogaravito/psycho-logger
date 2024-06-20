package com.pablogb.psychologger.controller.gui.view;

import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.utils.DateUtils;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionListViewShort {
    private Long id;
    private String sessionDate;
    private String themes;

    public static SessionListViewShort create(SessionEntity sessionEntity) {
        return SessionListViewShort.builder()
                .id(sessionEntity.getId())
                .sessionDate(DateUtils.formatIntermediateDate(sessionEntity.getSessionDate()))
                .themes(sessionEntity.getThemes())
                .build();
    }
}
