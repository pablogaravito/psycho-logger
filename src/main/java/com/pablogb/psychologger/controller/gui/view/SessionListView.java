package com.pablogb.psychologger.controller.gui.view;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.utils.DateUtils;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionListView {
    private Long id;

    private String sessionDate;

    private String themes;

    private List<PatientShort> patients;

    public static SessionListView create(SessionEntity sessionEntity, Function<Set<PatientEntity>, List<PatientShort>> patientNameRetriever) {
        return SessionListView.builder()
                .id(sessionEntity.getId())
                .sessionDate(DateUtils.formatIntermediateDate(sessionEntity.getSessionDate()))
                .themes(sessionEntity.getThemes())
                .patients(patientNameRetriever.apply(sessionEntity.getPatients()))
                .build();
    }
}
