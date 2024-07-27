package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.api.SessionLiteDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.api.CreateSessionDto;
import com.pablogb.psychologger.model.entity.SessionEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SessionService {

    SessionDto getSession(Long id);
    List<SessionDto> getSessions();
    List<SessionLiteDto> getPatientSessions(Long id);
    SessionDto saveSession(CreateSessionDto createSessionDto);
    SessionDto updateSession(Long id, CreateSessionDto createSessionDto);
    SessionDto partialUpdateSession(Long id, SessionDto sessionDto);
    void deleteSession(Long id);
    boolean sessionExists(Long id);
    Page<SessionDto> getSessionsPaginated(int page, int size);
    Page<SessionDto> getSessionsPaginated(String keyword, int page, int size);
    Page<SessionDto> getPatientSessionsPaginated(Long id, int page, int size);
}
