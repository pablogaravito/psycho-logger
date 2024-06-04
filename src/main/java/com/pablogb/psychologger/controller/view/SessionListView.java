package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import lombok.*;

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

    private String subject;

    private String patients;

    public static SessionListView create(SessionEntity sessionEntity, Function<Set<PatientEntity>, String> patientNameRetriever) {
        return SessionListView.builder()
                .id(sessionEntity.getId())
                .sessionDate(String.valueOf(sessionEntity.getSessionDate()))
                .subject(sessionEntity.getSubject())
                .patients(patientNameRetriever.apply(sessionEntity.getPatients()))
                .build();
    }
}
