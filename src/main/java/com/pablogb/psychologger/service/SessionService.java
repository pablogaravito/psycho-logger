package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.dao.PatchSessionDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;

import java.util.Set;

public interface SessionService {

    SessionEntity getSession(Long id);
    Set<SessionEntity> getSessions();
    Set<PatientEntity> getPatients();
    SessionEntity saveSession(SessionEntity sessionEntity);
    SessionEntity partialUpdateSession(PatchSessionDto patchSessionDto);
    void deleteSession(Long id);
    boolean sessionExists(Long id);



    //Set<Session> getLast10Sessions();

}
