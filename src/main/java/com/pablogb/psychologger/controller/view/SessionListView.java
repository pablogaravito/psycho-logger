package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return SessionListView.builder()
                .id(sessionEntity.getId())
                .sessionDate(sessionEntity.getSessionDate().format(format))
                .subject(sessionEntity.getSubject())
                .patients(patientNameRetriever.apply(sessionEntity.getPatients()))
                .build();
    }
}
