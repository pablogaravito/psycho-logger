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
import com.pablogb.psychologger.dto.response.BirthdayPatientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> getAllPatients() {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getIsAdmin()) {
            // admin sees all org patients but basic info only
            return patientRepository
                    .findByOrganizationId(currentUser.getOrganization().getId())
                    .stream()
                    .map(this::toBasicResponseDto)
                    .toList();
        } else {
            // therapist sees only their assigned patients with full info
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
        User currentUser = securityUtils.getCurrentUser();
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + id));

        // check org matches
        if (!patient.getOrganization().getId()
                .equals(currentUser.getOrganization().getId())) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }

        // admin gets basic info only
        if (currentUser.getIsAdmin() && !currentUser.getIsTherapist()) {
            return toBasicResponseDto(patient);
        }

        // therapist must be assigned to this patient
        if (currentUser.getIsTherapist()) {
            boolean isAssigned = assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNull(currentUser.getId())
                    .stream()
                    .anyMatch(a -> a.getPatient().getId().equals(id));

            if (!isAssigned && !currentUser.getIsAdmin()) {
                throw new ResourceNotFoundException(
                        "Access denied — patient not assigned to you");
            }

            // admin+therapist gets full info for their patients
            // pure admin gets basic info
            if (isAssigned) {
                return toResponseDto(patient);
            } else {
                return toBasicResponseDto(patient);
            }
        }

        return toBasicResponseDto(patient);
    }

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
                .calendarColor(request.getCalendarColor() != null ? request.getCalendarColor() : 7)
                .build();

        Patient saved = patientRepository.save(patient);

        boolean shouldAssign = currentUser.getIsTherapist()
                && Boolean.TRUE.equals(request.getAssignToMe());

        if (shouldAssign) {
            createAssignment(currentUser, saved);
        }

        return toResponseDto(saved);
    }

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

        if (request.getCalendarColor() != null)
            patient.setCalendarColor(request.getCalendarColor());

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

    @Override
    @Transactional(readOnly = true)
    public List<BirthdayPatientDto> getUpcomingBirthdays(boolean includeInactive) {
        User currentUser = securityUtils.getCurrentUser();

        List<Patient> patients;

        LocalDate today = LocalDate.now();

        if (currentUser.getIsAdmin()) {
            // admin sees all org patients
            patients = includeInactive
                    ? patientRepository.findByOrganizationId(
                    currentUser.getOrganization().getId())
                    : patientRepository.findByOrganizationIdAndIsActiveTrue(
                    currentUser.getOrganization().getId());
        } else {
            // therapist sees only their assigned patients
            patients = assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNull(currentUser.getId())
                    .stream()
                    .map(a -> a.getPatient())
                    .filter(p -> includeInactive || p.getIsActive())
                    .toList();
        }

        return patients.stream()
                .filter(p -> p.getDateOfBirth() != null)
                .map(p -> {
                    LocalDate dob = p.getDateOfBirth();
                    // get birthday this year
                    LocalDate birthdayThisYear = dob.withYear(today.getYear());

                    // if birthday already passed this year, check next year too
                    int daysUntil = (int) java.time.temporal.ChronoUnit.DAYS
                            .between(today, birthdayThisYear);

                    // if more than 21 days away this year,
                    // check if it already happened and use last year
                    if (daysUntil > 21) {
                        LocalDate birthdayLastYear = dob.withYear(today.getYear() - 1);
                        int daysUntilLast = (int) java.time.temporal.ChronoUnit.DAYS
                                .between(today, birthdayLastYear);
                        if (daysUntilLast >= -7) {
                            daysUntil = daysUntilLast;
                            birthdayThisYear = birthdayLastYear;
                        }
                    }

                    // filter to window: -7 to +21 days
                    if (daysUntil < -7 || daysUntil > 21) return null;

                    int age = today.getYear() - dob.getYear();
                    if (today.isBefore(birthdayThisYear)) age--;

                    return BirthdayPatientDto.builder()
                            .patientId(p.getId())
                            .patientName(p.getFirstName() + " " + p.getLastName())
                            .shortName(p.getShortName())
                            .isActive(p.getIsActive())
                            .dateOfBirth(dob)
                            .age(age)
                            .daysUntil(daysUntil)
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .sorted(java.util.Comparator.comparingInt(BirthdayPatientDto::getDaysUntil))
                .toList();
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
                .calendarColor(patient.getCalendarColor())
                .build();
    }

    private PatientResponseDto toResponseDto(Patient patient) {
        // find current active assignment
        String assignedTherapistName = null;
        Integer assignedTherapistId = null;

        var activeAssignment = assignmentRepository
                .findByPatientIdOrderByAssignedAtDesc(patient.getId())
                .stream()
                .filter(a -> a.getUnassignedAt() == null)
                .findFirst();

        if (activeAssignment.isPresent()) {
            User therapist = activeAssignment.get().getTherapist();
            assignedTherapistName = therapist.getFirstName() + " " + therapist.getLastName();
            assignedTherapistId = therapist.getId();
        }

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
                .defaultPrice(patient.getDefaultPrice())
                .hasDebtFlag(patient.getHasDebtFlag())
                .debtFlagNote(patient.getDebtFlagNote())
                .handoverNotes(patient.getHandoverNotes())
                .assignedTherapistName(assignedTherapistName)
                .assignedTherapistId(assignedTherapistId)
                .createdAt(patient.getCreatedAt())
                .build();
    }
}