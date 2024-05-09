package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;


    @Override
    public SessionEntity getSession(Long id) {
        Optional<SessionEntity> session = sessionRepository.findById(id);
        return unwrapSession(session, id);
    }

    @Override
    public SessionEntity saveSession(SessionEntity sessionEntity) {
        return sessionRepository.save(sessionEntity);
    }

    @Override
    public SessionEntity updateSession(Long id, SessionEntity sessionEntity) {
        sessionEntity.setId(id);
        return sessionRepository.save(sessionEntity);
    }

    @Override
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    static SessionEntity unwrapSession(Optional<SessionEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, SessionEntity.class);
    }

}
