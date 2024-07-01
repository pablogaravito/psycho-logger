package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.dto.PatchSessionDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface SessionService {

    SessionEntity getSession(Long id);
    Set<SessionEntity> getSessions();
    Set<PatientEntity> getPatients(Long id);
    SessionEntity saveSession(SessionEntity sessionEntity);
    SessionEntity partialUpdateSession(PatchSessionDto patchSessionDto);
    void deleteSession(Long id);
    boolean sessionExists(Long id);
    Page<SessionEntity> getSessionsPaginated(int page, int size);
}
