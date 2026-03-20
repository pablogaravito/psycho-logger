package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.PatientRequestDto;
import com.pablogb.psychologger.dto.response.PatientResponseDto;
import com.pablogb.psychologger.exception.ResourceNotFoundException;
import com.pablogb.psychologger.model.entity.Organization;
import com.pablogb.psychologger.model.entity.Patient;
import com.pablogb.psychologger.model.entity.TherapistPatientAssignment;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.repository.OrganizationRepository;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.TherapistPatientAssignmentRepository;
import com.pablogb.psychologger.repository.UserRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final TherapistPatientAssignmentRepository assignmentRepository;
    private final SecurityUtils securityUtils;

//    @Override
//    @Transactional(readOnly = true)
//    public List<PatientResponseDto> getAllPatients() {
//        return patientRepository.findByOrganizationId(securityUtils.getCurrentOrgId())
//                .stream()
//                .map(this::toResponseDto)
//                .toList();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> getAllPatients() {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getIsAdmin()) {
            // admin sees all org patients
            return patientRepository.findByOrganizationId(currentUser.getOrganization().getId())
                    .stream()
                    .map(this::toResponseDto)
                    .toList();
        } else {
            // therapist sees only their assigned patients
            return assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNull(currentUser.getId())
                    .stream()
                    .map(a -> toResponseDto(a.getPatient()))
                    .toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return toResponseDto(patient);
    }

//    @Override
//    @Transactional
//    public PatientResponseDto createPatient(PatientRequestDto request) {
//
//        User currentUser = securityUtils.getCurrentUser();
//        Organization org = currentUser.getOrganization();
//
//        Patient patient = Patient.builder()
//                .organization(org)
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .shortName(request.getShortName())
//                .email(request.getEmail())
//                .phone(request.getPhone())
//                .dateOfBirth(request.getDateOfBirth())
//                .gender(request.getGender())
//                .notes(request.getNotes())
//                .defaultPrice(request.getDefaultPrice())
//                .isActive(true)
//                .build();
//
//        Patient saved = patientRepository.save(patient);
//
//        // create assignment to current therapist
//        TherapistPatientAssignment assignment = TherapistPatientAssignment.builder()
//                .therapist(currentUser)
//                .patient(saved)
//                .assignedAt(LocalDateTime.now())
//                .build();
//        assignmentRepository.save(assignment);
//
//        return toResponseDto(saved);
//    }

    @Override
    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto request) {
        User currentUser = securityUtils.getCurrentUser();
        Organization org = currentUser.getOrganization();

        Patient patient = Patient.builder()
                .organization(org)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .shortName(request.getShortName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .notes(request.getNotes())
                .defaultPrice(request.getDefaultPrice())
                .hasDebtFlag(false)
                .isActive(true)
                .build();

        Patient saved = patientRepository.save(patient);

        // auto-assign if solo org, otherwise only assign if admin creating for themselves
        long therapistCount = userRepository.countByOrganizationIdAndIsTherapistTrue(
                org.getId());

        if (therapistCount == 1) {
            // solo — auto assign to the only therapist
            createAssignment(currentUser, saved);
        } else if (currentUser.getIsTherapist()) {
            // multi-therapist org, creating therapist assigns to themselves
            createAssignment(currentUser, saved);
        }
        // if pure admin in multi-therapist org, assignment done separately

        return toResponseDto(saved);
    }

//    @Override
//    @Transactional
//    public PatientResponseDto updatePatient(Integer id, PatientRequestDto request) {
//        Patient patient = patientRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
//
//        patient.setFirstName(request.getFirstName());
//        patient.setLastName(request.getLastName());
//        patient.setShortName(request.getShortName());
//        patient.setEmail(request.getEmail());
//        patient.setPhone(request.getPhone());
//        patient.setDateOfBirth(request.getDateOfBirth());
//        patient.setGender(request.getGender());
//        patient.setNotes(request.getNotes());
//        patient.setDefaultPrice(request.getDefaultPrice());
//        if (request.getIsActive() != null) {
//            patient.setIsActive(request.getIsActive());
//        }
//
//        return toResponseDto(patientRepository.save(patient));
//    }

    @Override
    @Transactional
    public PatientResponseDto updatePatient(Integer id, PatientRequestDto request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setShortName(request.getShortName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setNotes(request.getNotes());
        patient.setDefaultPrice(request.getDefaultPrice());
        patient.setHandoverNotes(request.getHandoverNotes());

        if (request.getIsActive() != null)
            patient.setIsActive(request.getIsActive());

        // only allow flag changes through explicit flag endpoint
        return toResponseDto(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public void deactivatePatient(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        patient.setIsActive(false);
        patientRepository.save(patient);
    }

    private void createAssignment(User therapist, Patient patient) {
        TherapistPatientAssignment assignment = TherapistPatientAssignment.builder()
                .therapist(therapist)
                .patient(patient)
                .assignedAt(LocalDateTime.now())
                .build();
        assignmentRepository.save(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> searchPatientsOrgWide(String name) {
        Integer orgId = securityUtils.getCurrentOrgId();
        return patientRepository.searchByOrgAndName(orgId, name)
                .stream()
                .map(this::toBasicResponseDto)  // basic info only
                .toList();
    }

    @Override
    @Transactional
    public PatientResponseDto flagPatient(Integer id, String note) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
        patient.setHasDebtFlag(true);
        patient.setDebtFlagNote(note);
        return toResponseDto(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public PatientResponseDto clearFlag(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
        patient.setHasDebtFlag(false);
        patient.setDebtFlagNote(null);
        return toResponseDto(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public PatientResponseDto assignPatient(Integer patientId, Integer therapistId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new ResourceNotFoundException("Therapist not found: " + therapistId));

        // close any existing active assignment
        assignmentRepository.findByPatientIdOrderByAssignedAtDesc(patientId)
                .stream()
                .filter(a -> a.getUnassignedAt() == null)
                .forEach(a -> {
                    a.setUnassignedAt(LocalDateTime.now());
                    assignmentRepository.save(a);
                });

        // create new assignment
        createAssignment(therapist, patient);
        return toResponseDto(patient);
    }

    @Override
    @Transactional
    public void unassignPatient(Integer patientId) {
        assignmentRepository.findByPatientIdOrderByAssignedAtDesc(patientId)
                .stream()
                .filter(a -> a.getUnassignedAt() == null)
                .forEach(a -> {
                    a.setUnassignedAt(LocalDateTime.now());
                    assignmentRepository.save(a);
                });
    }

    // basic info only — no clinical notes
    private PatientResponseDto toBasicResponseDto(Patient patient) {
        // get assignment history
        List<TherapistPatientAssignment> assignments = assignmentRepository
                .findByPatientIdOrderByAssignedAtDesc(patient.getId());

        return PatientResponseDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .shortName(patient.getShortName())
                .hasDebtFlag(patient.getHasDebtFlag())
                .debtFlagNote(patient.getDebtFlagNote())
                .isActive(patient.getIsActive())
                .createdAt(patient.getCreatedAt())
                .build();
    }

    private PatientResponseDto toResponseDto(Patient patient) {
        return PatientResponseDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .shortName(patient.getShortName())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .isActive(patient.getIsActive())
                .notes(patient.getNotes())
                .createdAt(patient.getCreatedAt())
                .defaultPrice(patient.getDefaultPrice())
                .hasDebtFlag(patient.getHasDebtFlag())
                .debtFlagNote(patient.getDebtFlagNote())
                .handoverNotes(patient.getHandoverNotes())
                .build();
    }
}