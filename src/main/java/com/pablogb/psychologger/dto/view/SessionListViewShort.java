package com.pablogb.psychologger.dto.view;

import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.util.DateUtils;
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
