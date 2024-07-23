package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.api.CreateSessionDto;
import com.pablogb.psychologger.model.entity.SessionEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SessionService {

    SessionDto getSession(Long id);
    List<SessionDto> getSessions();
    List<SessionDto> getPatientSessions(Long id);
    SessionDto saveSession(CreateSessionDto CreateSessionDto);
    SessionDto updateSession(CreateSessionDto CreateSessionDto);
    SessionDto partialUpdateSession(SessionDto sessionDto);
    void deleteSession(Long id);
    boolean sessionExists(Long id);
    Page<SessionEntity> getSessionsPaginated(int page, int size);
    Page<SessionEntity> getSessionsPaginated(String keyword, int page, int size);
    Page<SessionEntity> getPatientSessionsPaginated(Long id, int page, int size);
}
