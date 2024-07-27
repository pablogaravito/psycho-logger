package com.pablogb.psychologger.dto.view;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.util.DateUtils;
import lombok.*;

import java.util.List;
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

//    public static SessionListView create(SessionEntity sessionEntity, Function<List<PatientEntity>, List<PatientShort>> patientNameRetriever) {
//        return SessionListView.builder()
//                .id(sessionEntity.getId())
//                .sessionDate(DateUtils.formatIntermediateDate(sessionEntity.getSessionDate()))
//                .themes(sessionEntity.getThemes())
//                .patients(patientNameRetriever.apply(sessionEntity.getPatients()))
//                .build();
//    }
    public static SessionListView createFromDto(SessionDto sessionDto, Function<List<PatientDto>, List<PatientShort>> patientNameRetriever) {
        System.out.println(sessionDto.getId());
        return SessionListView.builder()
                .id(sessionDto.getId())
                .sessionDate(DateUtils.formatIntermediateDate(sessionDto.getSessionDate()))
                .themes(sessionDto.getThemes())
                .patients(patientNameRetriever.apply(sessionDto.getPatients()))
                .build();
    }

}
