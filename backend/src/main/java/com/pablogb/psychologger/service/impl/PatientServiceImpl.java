package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.PatientRequestDto;
import com.pablogb.psychologger.dto.response.PatientResponseDto;
import com.pablogb.psychologger.model.entity.Organization;
import com.pablogb.psychologger.model.entity.Patient;
import com.pablogb.psychologger.model.entity.TherapistPatientAssignment;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.repository.OrganizationRepository;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.TherapistPatientAssignmentRepository;
import com.pablogb.psychologger.repository.UserRepository;
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

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        return toResponseDto(patient);
    }

    @Override
    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto request) {
        // hardcoded for now, will come from auth context later
        Organization org = organizationRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        User therapist = userRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

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
                .isActive(true)
                .build();

        Patient saved = patientRepository.save(patient);

        // create assignment to current therapist
        TherapistPatientAssignment assignment = TherapistPatientAssignment.builder()
                .therapist(therapist)
                .patient(saved)
                .assignedAt(LocalDateTime.now())
                .build();
        assignmentRepository.save(assignment);

        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public PatientResponseDto updatePatient(Integer id, PatientRequestDto request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setShortName(request.getShortName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setNotes(request.getNotes());

        return toResponseDto(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public void deactivatePatient(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        patient.setIsActive(false);
        patientRepository.save(patient);
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
                .build();
    }
}