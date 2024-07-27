package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.api.SessionCreationDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.api.SessionLiteDto;
import com.pablogb.psychologger.exception.EmptyPatientListException;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final PatientService patientService;
    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final Mapper<PatientEntity, PatientDto> patientDtoMapper;
    private final Mapper<SessionEntity, SessionDto> sessionDtoMapper;
    private final Mapper<SessionEntity, SessionLiteDto> sessionLiteDtoMapper;
    private final Mapper<SessionEntity, SessionCreationDto> createSessionDtoMapper;

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
    public List<SessionLiteDto> getPatientSessions(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new EntityNotFoundException(id, PatientEntity.class);
        }
        return sessionRepository.getSessionsFromPatient(id).stream().map(sessionLiteDtoMapper::mapTo).toList();
    }

    @Override
    public SessionDto saveSession(SessionCreationDto sessionCreationDto) {
        SessionEntity sessionEntity = createSessionDtoMapper.mapFrom(sessionCreationDto);
        checkAndAddPatientsByIds(sessionEntity, sessionCreationDto.getPatients());
        SessionEntity savedSessionEntity = sessionRepository.save(sessionEntity);
        return sessionDtoMapper.mapTo(savedSessionEntity);
    }

    @Override
    public SessionDto updateSession(Long id, SessionCreationDto sessionCreationDto) {
        return sessionRepository.findById(id)
                .map(existingSession -> {
                    updateSessionFields(existingSession, sessionCreationDto);
//                    checkAndAddPatientsByIds(existingSession, createSessionDto.getPatients());
                    SessionEntity updatedSession = sessionRepository.save(existingSession);
                    return sessionDtoMapper.mapTo(updatedSession);

                }).orElseThrow(() -> new EntityNotFoundException(id, SessionEntity.class));
    }

    @Override
    public SessionDto partialUpdateSession(Long id, SessionDto sessionDto) {

        return sessionRepository.findById(id)
                .map(existingSession -> {
                    updateSessionFields(existingSession, sessionDto);
//                    checkAndAddPatientsByDtos(existingSession, sessionDto.getPatients());
                    SessionEntity updatedSession = sessionRepository.save(existingSession);
                    return sessionDtoMapper.mapTo(updatedSession);
                })
                .orElseThrow(() -> new EntityNotFoundException(id, SessionEntity.class));
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
    public Page<SessionDto> getSessionsPaginated(int page, int size) {
        Page<SessionEntity> sessionsPage = sessionRepository.findAll(PageRequest.of(page, size));
        return sessionsPage.map(SessionDto::create);
    }

    @Override
    public Page<SessionDto> getSessionsPaginated(String keyword, int page, int size) {
        Page<SessionEntity> sessionsPage = sessionRepository.findSessionsByKeyword(keyword, PageRequest.of(page, size));
        return sessionsPage.map(SessionDto::create);
    }

    @Override
    public Page<SessionDto> getPatientSessionsPaginated(Long patientId, int page, int size) {
        Page<SessionEntity> sessionsPage = sessionRepository.getSessionsFromPatientPaginated(patientId, PageRequest.of(page, size));
        return sessionsPage.map(SessionDto::create);
    }

    private void updateSessionFields(SessionEntity existingSession, SessionDto sessionDto) {
        Optional.ofNullable(sessionDto.getThemes()).ifPresent(existingSession::setThemes);
        Optional.ofNullable(sessionDto.getContent()).ifPresent(existingSession::setContent);
        Optional.ofNullable(sessionDto.getSessionDate()).ifPresent(existingSession::setSessionDate);
        Optional.ofNullable(sessionDto.getIsImportant()).ifPresent(existingSession::setIsImportant);
        Optional.ofNullable(sessionDto.getIsPaid()).ifPresent(existingSession::setIsPaid);
        Optional.ofNullable(sessionDto.getNextWeek()).ifPresent(existingSession::setNextWeek);
    }

    private void updateSessionFields(SessionEntity existingSession, SessionCreationDto sessionCreationDto) {
        existingSession.setThemes(sessionCreationDto.getThemes());
        existingSession.setContent(sessionCreationDto.getContent());
        existingSession.setSessionDate(sessionCreationDto.getSessionDate());
        existingSession.setIsPaid(sessionCreationDto.getIsPaid());
        existingSession.setIsImportant(sessionCreationDto.getIsImportant());
        Optional.ofNullable(sessionCreationDto.getNextWeek()).ifPresent(existingSession::setNextWeek);
    }

    private void checkAndAddPatientsByIds(SessionEntity sessionEntity, List<Long> patientIds) {
        if (patientIds.isEmpty()) throw new EmptyPatientListException();
        List<PatientDto> patients = new ArrayList<>();
        for (Long id : patientIds) {
            if (!patientService.patientExists(id)) throw new EntityNotFoundException(id, PatientEntity.class);
            patients.add(patientService.getPatient(id));
        }
        sessionEntity.setPatients(patients.stream().map(patientDtoMapper::mapFrom).toList());
    }

    private void checkAndAddPatientsByDtos(SessionEntity sessionEntity, List<PatientDto> patientDtos) {
        if (patientDtos.isEmpty()) throw new EmptyPatientListException();
        List<PatientDto> patients = new ArrayList<>();
        for (PatientDto patientDto : patientDtos) {
            Long id = patientDto.getId();
            if (!patientService.patientExists(id)) throw new EntityNotFoundException(id, PatientEntity.class);
            patients.add(patientService.getPatient(id));
        }
        sessionEntity.setPatients(patients.stream().map(patientDtoMapper::mapFrom).toList());
    }

    static SessionEntity unwrapSession(Optional<SessionEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, SessionEntity.class);
    }
}
