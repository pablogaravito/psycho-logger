package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.SessionRequestDto;
import com.pablogb.psychologger.dto.response.PageResponseDto;
import com.pablogb.psychologger.dto.response.PatientSummaryDto;
import com.pablogb.psychologger.dto.response.SessionResponseDto;
import com.pablogb.psychologger.exception.ResourceNotFoundException;
import com.pablogb.psychologger.model.entity.Patient;
import com.pablogb.psychologger.model.entity.Session;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.model.enums.AuditAction;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.PaymentRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.AuditService;
import com.pablogb.psychologger.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final PaymentRepository paymentRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<SessionResponseDto> getAllSessions(int page, int size) {
        User currentUser = securityUtils.getCurrentUser();

        if (!currentUser.getIsTherapist()) {
            throw new ResourceNotFoundException(
                    "Access denied — sessions are only visible to therapists");
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<Session> sessionPage = sessionRepository
                .findByTherapistIdOrderByScheduledAtDesc(
                        currentUser.getId(), pageable);

        return toPageResponse(sessionPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<SessionResponseDto> getSessionsByPatient(
            Integer patientId, int page, int size) {
        User currentUser = securityUtils.getCurrentUser();

        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                    "Patient not found with id: " + patientId);
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<Session> sessionPage = sessionRepository
                .findByPatientsIdAndTherapistIdOrderByScheduledAtDesc(
                        patientId, currentUser.getId(), pageable);

        return toPageResponse(sessionPage);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDto getSessionById(Integer id) {
        User currentUser = securityUtils.getCurrentUser();
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + id));

        // only the session's therapist can see it
        if (!session.getTherapist().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException(
                    "Access denied — you can only view your own sessions");
        }

        auditService.log(AuditAction.VIEW, "Session", id);
        return toResponseDto(session);
    }



    @Override
    @Transactional
    public SessionResponseDto createSession(SessionRequestDto request) {
        User currentUser = securityUtils.getCurrentUser();

        Set<Patient> patients = request.getPatientIds().stream()
                .map(pid -> patientRepository.findById(pid)
                        .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + pid)))
                .collect(Collectors.toSet());

        Session session = Session.builder()
                .organization(currentUser.getOrganization())
                .therapist(currentUser)
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes())
                .mainThemes(request.getMainThemes())
                .content(request.getContent())
                .nextSession(request.getNextSession())
                .isRelevant(request.getIsRelevant())
                .sessionType(request.getSessionType())
                .status(request.getStatus())
                .patients(patients)
                .build();

        Session savedSession = sessionRepository.save(session);
        auditService.log(AuditAction.CREATE, "Session", savedSession.getId());
        return toResponseDto(savedSession);
    }

    @Override
    @Transactional
    public SessionResponseDto updateSession(Integer id, SessionRequestDto request) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));

        Set<Patient> patients = request.getPatientIds().stream()
                .map(pid -> patientRepository.findById(pid)
                        .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + pid)))
                .collect(Collectors.toSet());

        session.setScheduledAt(request.getScheduledAt());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setMainThemes(request.getMainThemes());
        session.setContent(request.getContent());
        session.setNextSession(request.getNextSession());
        session.setIsRelevant(request.getIsRelevant());
        session.setSessionType(request.getSessionType());
        session.setStatus(request.getStatus());
        session.setPatients(patients);

        Session updatedSesion = sessionRepository.save(session);
        auditService.log(AuditAction.UPDATE, "Session", id);
        return toResponseDto(updatedSesion);
    }

    @Override
    @Transactional
    public void deleteSession(Integer id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));
        paymentRepository.deleteBySessionId(session.getId());
        sessionRepository.delete(session);
        auditService.log(AuditAction.DELETE, "Session", id,
                "Session and linked payments deleted");
    }



    private SessionResponseDto toResponseDto(Session session) {
        Set<PatientSummaryDto> patientSummaries = session.getPatients().stream()
                .map(p -> PatientSummaryDto.builder()
                        .id(p.getId())
                        .firstName(p.getFirstName())
                        .lastName(p.getLastName())
                        .shortName(p.getShortName())
                        .build())
                .collect(Collectors.toSet());

        return SessionResponseDto.builder()
                .id(session.getId())
                .scheduledAt(session.getScheduledAt())
                .durationMinutes(session.getDurationMinutes())
                .mainThemes(session.getMainThemes())
                .content(session.getContent())
                .nextSession(session.getNextSession())
                .isRelevant(session.getIsRelevant())
                .sessionType(session.getSessionType())
                .status(session.getStatus())
                .patients(patientSummaries)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    private PageResponseDto<SessionResponseDto> toPageResponse(Page<Session> page) {
        return PageResponseDto.<SessionResponseDto>builder()
                .content(page.getContent().stream()
                        .map(this::toResponseDto)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
