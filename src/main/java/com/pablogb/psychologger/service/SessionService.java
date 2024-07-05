package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.dto.PatchSessionDto;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SessionService {

    SessionEntity getSession(Long id);
    List<SessionEntity> getSessions();
    List<SessionEntity> getPatientSessions(Long id);
    SessionEntity saveSession(SessionEntity sessionEntity);
    SessionEntity partialUpdateSession(PatchSessionDto patchSessionDto);
    void deleteSession(Long id);
    boolean sessionExists(Long id);
    Page<SessionEntity> getSessionsPaginated(int page, int size);
    Page<SessionEntity> getSessionsPaginated(String keyword, int page, int size);
    Page<SessionEntity> getPatientSessionsPaginated(Long id, int page, int size);
}
