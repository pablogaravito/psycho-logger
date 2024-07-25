package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.api.SessionWithPatientsDto;
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
    private final Mapper<SessionEntity, SessionWithPatientsDto> sessionWithPatientsDtoMapper;
    private final Mapper<SessionEntity, SessionDto> sessionDtoMapper;

    @Override
    public SessionWithPatientsDto getSession(Long id) {
        SessionEntity sessionEntity = unwrapSession(sessionRepository.findById(id), id);
        return sessionWithPatientsDtoMapper.mapTo(sessionEntity);
    }

    @Override
    public List<SessionWithPatientsDto> getSessions() {
        List<SessionEntity> sessions = new ArrayList<>();
        Iterable<SessionEntity> sessionEntities = sessionRepository.findAll();
        sessionEntities.forEach(e -> e.setPatients(patientRepository.getPatientsFromSession(e.getId())));
        sessionEntities.forEach(sessions::add);
        return sessions.stream().map(sessionWithPatientsDtoMapper::mapTo).toList();
    }

    @Override
    public List<SessionDto> getPatientSessions(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException(id, PatientEntity.class);
        }
        return sessionRepository.getSessionsFromPatient(id).stream().map(sessionDtoMapper::mapTo).toList();
    }

    @Override
    public SessionWithPatientsDto saveSession(CreateSessionDto createSessionDto) {
        SessionWithPatientsDto sessionWithPatientsDto = SessionWithPatientsDto.create(createSessionDto);
        SessionEntity sessionEntity = sessionWithPatientsDtoMapper.mapFrom(sessionWithPatientsDto);

        List<PatientEntity> patients = new ArrayList<>();
        for (Long id : createSessionDto.getPatients()) {
            if (!patientService.patientExists(id)) throw new EntityNotFoundException(id, PatientEntity.class);
            PatientEntity patient = patientDtoMapper.mapFrom(patientService.getPatient(id));
            patients.add(patient);
        }
        sessionEntity.setPatients(patients);
        SessionEntity savedSessionEntity = sessionRepository.save(sessionEntity);
        return sessionWithPatientsDtoMapper.mapTo(savedSessionEntity);
    }

    @Override
    public SessionWithPatientsDto updateSession(CreateSessionDto createSessionDto) {
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
    public SessionWithPatientsDto partialUpdateSession(SessionWithPatientsDto sessionWithPatientsDto) {
        return sessionRepository.findById(sessionWithPatientsDto.getId())
                .map(existingSession -> {
                    updateSessionFields(existingSession, sessionWithPatientsDto);
                    updatePatients(existingSession, sessionWithPatientsDto);
                    SessionEntity updatedSession = sessionRepository.save(existingSession);
                    return sessionWithPatientsDtoMapper.mapTo(updatedSession);
                })
                .orElseThrow(() -> new EntityNotFoundException(sessionWithPatientsDto.getId(), SessionEntity.class));
    }

    private void updateSessionFields(SessionEntity existingSession, SessionWithPatientsDto sessionWithPatientsDto) {
        Optional.ofNullable(sessionWithPatientsDto.getThemes()).ifPresent(existingSession::setThemes);
        Optional.ofNullable(sessionWithPatientsDto.getContent()).ifPresent(existingSession::setContent);
        Optional.ofNullable(sessionWithPatientsDto.getIsImportant()).ifPresent(existingSession::setIsImportant);
        Optional.ofNullable(sessionWithPatientsDto.getIsPaid()).ifPresent(existingSession::setIsPaid);
        Optional.ofNullable(sessionWithPatientsDto.getNextWeek()).ifPresent(existingSession::setNextWeek);
    }

    private void updatePatients(SessionEntity existingSession, SessionWithPatientsDto sessionWithPatientsDto) {
        Optional.ofNullable(sessionWithPatientsDto.getPatients())
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
