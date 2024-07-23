package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.api.CreateSessionDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final PatientService patientService;
    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final  Mapper<PatientEntity, PatientDto> patientDtoMapper;
    private final Mapper<SessionEntity, SessionDto> sessionDtoMapper;

    @Override
    public SessionDto getSession(Long id) {
        SessionEntity sessionEntity = unwrapSession(sessionRepository.findById(id), id);
        return sessionDtoMapper.mapTo(sessionEntity);
    }

    @Override
    public List<SessionDto> getSessions() {
        List<SessionEntity> sessions = new ArrayList<>();
        Iterable<SessionEntity> sessionEntities = sessionRepository.findAll();
        sessionEntities.forEach(e -> e.setPatients(patientRepository.getPatientsFromSession(e.getId())));
        sessionEntities.forEach(sessions::add);
        return sessions.stream().map(sessionDtoMapper::mapTo).toList();
    }

    @Override
    public List<SessionDto> getPatientSessions(Long id) {
        return sessionRepository.getSessionsFromPatient(id).stream().map(sessionDtoMapper::mapTo).toList();
    }

    @Override
    public SessionDto saveSession(CreateSessionDto createSessionDto) {
        SessionDto sessionDto = SessionDto.create(createSessionDto);
        SessionEntity sessionEntity = sessionDtoMapper.mapFrom(sessionDto);

        List<PatientEntity> patients = new ArrayList<>();
        for (Long id : createSessionDto.getPatients()) {
            if (!patientService.patientExists(id)) throw new EntityNotFoundException(id, PatientEntity.class);
            PatientEntity patient = patientDtoMapper.mapFrom(patientService.getPatient(id));
            patients.add(patient);
        }
        sessionEntity.setPatients(patients);
        SessionEntity savedSessionEntity = sessionRepository.save(sessionEntity);
        return sessionDtoMapper.mapTo(savedSessionEntity);
    }

    @Override
    public SessionDto updateSession(CreateSessionDto createSessionDto) {
        return null;
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
    public SessionDto partialUpdateSession(SessionDto sessionDto) {
        return sessionRepository.findById(sessionDto.getId())
                .map(existingSession -> {
                    updateSessionFields(existingSession, sessionDto);
                    updatePatients(existingSession, sessionDto);
                    SessionEntity updatedSession = sessionRepository.save(existingSession);
                    return sessionDtoMapper.mapTo(updatedSession);
                })
                .orElseThrow(() -> new EntityNotFoundException(sessionDto.getId(), SessionEntity.class));
    }

    private void updateSessionFields(SessionEntity existingSession, SessionDto sessionDto) {
        Optional.ofNullable(sessionDto.getThemes()).ifPresent(existingSession::setThemes);
        Optional.ofNullable(sessionDto.getContent()).ifPresent(existingSession::setContent);
        Optional.ofNullable(sessionDto.getIsImportant()).ifPresent(existingSession::setIsImportant);
        Optional.ofNullable(sessionDto.getIsPaid()).ifPresent(existingSession::setIsPaid);
        Optional.ofNullable(sessionDto.getNextWeek()).ifPresent(existingSession::setNextWeek);
    }

    private void updatePatients(SessionEntity existingSession, SessionDto sessionDto) {
        Optional.ofNullable(sessionDto.getPatients())
                .ifPresent(patientDtos -> {
                    List<PatientEntity> patientEntities = patientDtos.stream()
                            .map(patientDtoMapper::mapFrom)
                            .collect(Collectors.toList());
                    existingSession.setPatients(patientEntities);
                });
    }

    static SessionEntity unwrapSession(Optional<SessionEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, SessionEntity.class);
    }
}
