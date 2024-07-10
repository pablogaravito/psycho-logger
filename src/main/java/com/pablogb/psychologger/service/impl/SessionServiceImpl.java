package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.api.PatchSessionDto;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public List<SessionEntity> getSessions() {
        List<SessionEntity> sessions = new ArrayList<>();
        Iterable<SessionEntity> sessionEntities = sessionRepository.findAll();
        sessionEntities.forEach(e -> e.setPatients(patientRepository.getPatientsFromSession(e.getId())));
        sessionEntities.forEach(sessions::add);
        return sessions;
    }

    @Override
    public List<SessionEntity> getPatientSessions(Long id) {
        return sessionRepository.getSessionsFromPatient(id);
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
    public Page<SessionEntity> getSessionsPaginated(int page, int size) {
        return sessionRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Page<SessionEntity> getSessionsPaginated(String keyword, int page, int size) {
        return sessionRepository.findSessionsByKeyword(keyword, PageRequest.of(page, size));
    }

    @Override
    public Page<SessionEntity> getPatientSessionsPaginated(Long patientId, int page, int size) {
        return sessionRepository.getSessionsFromPatientPaginated(patientId, PageRequest.of(page, size));
    }

    @Override
    public SessionEntity partialUpdateSession(PatchSessionDto patchSessionDto) {
        return sessionRepository.findById(patchSessionDto.getId()).map(existingSession -> {
            Optional.ofNullable(patchSessionDto.getThemes()).ifPresent(existingSession::setThemes);
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
