package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.domain.dto.PatchSessionDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;


    @Override
    public SessionEntity getSession(Long id) {
        Optional<SessionEntity> session = sessionRepository.findById(id);
        return unwrapSession(session, id);
    }

    @Override
    public Set<SessionEntity> getSessions() {
        Set<SessionEntity> sessions = new HashSet<>();
        Iterable<SessionEntity> sessionEntities = sessionRepository.findAll();
        sessionEntities.forEach(e -> e.setPatients(patientRepository.getPatientsFromSession(e.getId())));
        sessionEntities.forEach(sessions::add);
        return sessions;
    }

    @Override
    public Set<PatientEntity> getPatients(Long id) {
        SessionEntity sessionEntity = getSession(id);
        return sessionEntity.getPatients();
    }

    @Override
    public SessionEntity saveSession(SessionEntity sessionEntity) {
        return sessionRepository.save(sessionEntity);
    }

    @Override
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    @Override
    public boolean sessionExists(Long id) {
        return sessionRepository.existsById(id);
    }

    @Override
    public SessionEntity partialUpdateSession(PatchSessionDto patchSessionDto) {
        return sessionRepository.findById(patchSessionDto.getId()).map(existingSession -> {
            Optional.ofNullable(patchSessionDto.getSubject()).ifPresent(existingSession::setSubject);
            Optional.ofNullable(patchSessionDto.getContent()).ifPresent(existingSession::setContent);
            Optional.ofNullable(patchSessionDto.getIsImportant()).ifPresent(existingSession::setIsImportant);
            Optional.ofNullable(patchSessionDto.getIsPaid()).ifPresent(existingSession::setIsPaid);
            Optional.ofNullable(patchSessionDto.getNextWeek()).ifPresent(existingSession::setNextWeek);
            Optional.ofNullable(patchSessionDto.getPatients()).ifPresent(existingSession::setPatients);
            return sessionRepository.save(existingSession);
        }).orElseThrow(() -> new RuntimeException("Session does not exist"));
    }

    static SessionEntity unwrapSession(Optional<SessionEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, SessionEntity.class);
    }
}
