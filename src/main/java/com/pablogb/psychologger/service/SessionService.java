package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.api.SessionWithPatientsDto;
import com.pablogb.psychologger.dto.api.CreateSessionDto;
import com.pablogb.psychologger.model.entity.SessionEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SessionService {

    SessionWithPatientsDto getSession(Long id);
    List<SessionWithPatientsDto> getSessions();
    List<SessionDto> getPatientSessions(Long id);
    SessionWithPatientsDto saveSession(CreateSessionDto CreateSessionDto);
    SessionWithPatientsDto updateSession(CreateSessionDto CreateSessionDto);
    SessionWithPatientsDto partialUpdateSession(SessionWithPatientsDto sessionWithPatientsDto);
    void deleteSession(Long id);
    boolean sessionExists(Long id);
    Page<SessionEntity> getSessionsPaginated(int page, int size);
    Page<SessionEntity> getSessionsPaginated(String keyword, int page, int size);
    Page<SessionEntity> getPatientSessionsPaginated(Long id, int page, int size);
}
