package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.entity.Session;

public interface SessionService {

    Session getSession(Long id);
    Session saveSession(Session session);
    Session updateSession(Long id, Session session);
    void deleteSession(Long id);

    //Set<Session> getLast10Sessions();

}
