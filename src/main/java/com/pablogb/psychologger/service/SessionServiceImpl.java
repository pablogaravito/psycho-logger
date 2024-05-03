package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.entity.Session;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;


    @Override
    public Session getSession(Long id) {
        Optional<Session> session = sessionRepository.findById(id);
        return unwrapSession(session, id);
    }

    @Override
    public Session saveSession(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public Session updateSession(Long id, Session session) {
        session.setId(id);
        return sessionRepository.save(session);
    }

    @Override
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

//    @Override
//    public Set<Session> getLast10Sessions() {
//        return sessionRepository.getLast10Sessions();
//    }

    static Session unwrapSession(Optional<Session> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, Session.class);
    }

}
