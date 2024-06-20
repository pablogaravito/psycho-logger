package com.pablogb.psychologger.controller.gui.view;

import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SessionShort {
    private Long id;
    private String themes;
    private String sessionDate;

    public static SessionShort create(SessionEntity sessionEntity) {
        return new SessionShort(sessionEntity.getId(), sessionEntity.getThemes(), DateUtils.formatShortDate(sessionEntity.getSessionDate()));
    }
}
