package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.entity.SessionEntity;

public interface SessionService {

    SessionEntity getSession(Long id);
    SessionEntity saveSession(SessionEntity sessionEntity);
    SessionEntity updateSession(Long id, SessionEntity sessionEntity);
    void deleteSession(Long id);

    //Set<Session> getLast10Sessions();

}
