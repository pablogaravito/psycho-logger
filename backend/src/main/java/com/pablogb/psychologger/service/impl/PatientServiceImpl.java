package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.PatientRequestDto;
import com.pablogb.psychologger.dto.response.PageResponseDto;
import com.pablogb.psychologger.dto.response.PatientResponseDto;
import com.pablogb.psychologger.exception.ResourceNotFoundException;
import com.pablogb.psychologger.model.entity.*;
import com.pablogb.psychologger.model.enums.AuditAction;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import com.pablogb.psychologger.repository.*;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.AuditService;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.dto.response.BirthdayPatientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final TherapistPatientAssignmentRepository assignmentRepository;
    private final SecurityUtils securityUtils;
    private final PaymentRepository paymentRepository;
    private final AuditService auditService;


    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<PatientResponseDto> getAllPatients(
            int page, int size, boolean showInactive) {
        User currentUser = securityUtils.getCurrentUser();
        Integer orgId = currentUser.getOrganization().getId();
        PageRequest pageable = PageRequest.of(page, size);
        Page<Patient> patientPage;

        if (currentUser.getIsAdmin() && currentUser.getIsTherapist()) {
            // fetch assigned IDs for full vs basic info distinction
            Set<Integer> assignedPatientIds = assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNull(currentUser.getId())
                    .stream()
                    .map(a -> a.getPatient().getId())
                    .collect(Collectors.toSet());

            patientPage = showInactive
                    ? patientRepository
                    .findByOrganizationIdOrderByIsActiveDescLastNameAsc(
                            orgId, pageable)
                    : patientRepository
                    .findByOrganizationIdAndIsActiveTrueOrderByLastNameAsc(
                            orgId, pageable);

            return toPageResponse(patientPage, p ->
                    assignedPatientIds.contains(p.getId())
                            ? toResponseDto(p)
                            : toBasicResponseDto(p));

        } else if (currentUser.getIsAdmin()) {
            patientPage = showInactive
                    ? patientRepository
                    .findByOrganizationIdOrderByIsActiveDescLastNameAsc(
                            orgId, pageable)
                    : patientRepository
                    .findByOrganizationIdAndIsActiveTrueOrderByLastNameAsc(
                            orgId, pageable);

            return toPageResponse(patientPage, this::toBasicResponseDto);

        } else {
            // pure therapist — only their active assignments
            Page<TherapistPatientAssignment> assignmentPage = showInactive
                    ? assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNullOrderByPatientLastNameAsc(
                            currentUser.getId(), pageable)
                    : assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNullAndPatientIsActiveTrueOrderByPatientLastNameAsc(
                            currentUser.getId(), pageable);

            return PageResponseDto.<PatientResponseDto>builder()
                    .content(assignmentPage.getContent().stream()
                            .map(a -> toResponseDto(a.getPatient()))
                            .toList())
                    .page(assignmentPage.getNumber())
                    .size(assignmentPage.getSize())
                    .totalElements(assignmentPage.getTotalElements())
                    .totalPages(assignmentPage.getTotalPages())
                    .first(assignmentPage.isFirst())
                    .last(assignmentPage.isLast())
                    .build();
        }
    }

    @Override
    @Transactional
    public PatientResponseDto getPatientById(Integer id) {
        User currentUser = securityUtils.getCurrentUser();
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + id));

        if (!patient.getOrganization().getId()
                .equals(currentUser.getOrganization().getId())) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }

        PatientResponseDto result;

        if (currentUser.getIsAdmin() && !currentUser.getIsTherapist()) {
            result = toBasicResponseDto(patient);
        } else if (currentUser.getIsTherapist()) {
            boolean isAssigned = assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNull(currentUser.getId())
                    .stream()
                    .anyMatch(a -> a.getPatient().getId().equals(id));

            if (!isAssigned && !currentUser.getIsAdmin()) {
                throw new ResourceNotFoundException(
                        "Access denied — patient not assigned to you");
            }
            result = isAssigned ? toResponseDto(patient) : toBasicResponseDto(patient);
        } else {
            result = toBasicResponseDto(patient);
        }

        auditService.log(AuditAction.VIEW, "Patient", id);
        return result;
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

        auditService.log(AuditAction.CREATE, "Patient", saved.getId());

        boolean shouldAssign = currentUser.getIsTherapist()
                && Boolean.TRUE.equals(request.getAssignToMe());

        if (shouldAssign) {
            createAssignment(currentUser, saved);
        }

        return toResponseDto(saved);
    }

//    @Override
//    @Transactional
//    public PatientResponseDto updatePatient(Integer id, PatientRequestDto request) {
//        Patient patient = patientRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Patient not found with id: " + id));
//
//        // 1. track meaningful changes
//        List<String> changes = new ArrayList<>();
//
//        if (request.getIsActive() != null &&
//                !request.getIsActive().equals(patient.getIsActive())) {
//            changes.add("status: " + (request.getIsActive() ? "activated" : "deactivated"));
//        }
//        if (request.getDefaultPrice() != null &&
//                request.getDefaultPrice().compareTo(
//                        patient.getDefaultPrice() != null
//                                ? patient.getDefaultPrice()
//                                : BigDecimal.ZERO) != 0) {
//            changes.add("price: " + patient.getDefaultPrice()
//                    + " → " + request.getDefaultPrice());
//        }
//        if (request.getCalendarColor() != null &&
//                !request.getCalendarColor().equals(patient.getCalendarColor())) {
//            changes.add("calendar color changed");
//        }
//
//        // 2. apply changes
//        if (request.getFirstName() != null)
//            patient.setFirstName(request.getFirstName());
//        if (request.getLastName() != null)
//            patient.setLastName(request.getLastName());
//        if (request.getShortName() != null)
//            patient.setShortName(request.getShortName());
//        if (request.getEmail() != null)
//            patient.setEmail(request.getEmail());
//        if (request.getPhone() != null)
//            patient.setPhone(request.getPhone());
//        if (request.getDateOfBirth() != null)
//            patient.setDateOfBirth(request.getDateOfBirth());
//        if (request.getGender() != null)
//            patient.setGender(request.getGender());
//        if (request.getNotes() != null)
//            patient.setNotes(request.getNotes());
//        if (request.getDefaultPrice() != null)
//            patient.setDefaultPrice(request.getDefaultPrice());
//        if (request.getHandoverNotes() != null)
//            patient.setHandoverNotes(request.getHandoverNotes());
//        if (request.getIsActive() != null)
//            patient.setIsActive(request.getIsActive());
//        if (request.getCalendarColor() != null)
//            patient.setCalendarColor(request.getCalendarColor());
//
//        // 3. save
//        Patient updatedPatient = patientRepository.save(patient);
//
//        // 4. log with details
//        String details = changes.isEmpty() ? "General info updated" : String.join(", ", changes);
//        auditService.log(AuditAction.UPDATE, "Patient", id, details);
//
//        return toResponseDto(updatedPatient);
//    }

    @Override
    @Transactional
    public PatientResponseDto updatePatient(Integer id, PatientRequestDto request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        List<String> changes = new ArrayList<>();

        // 1. Standard Fields (Cleaner method calls)
        update(request.getFirstName(), patient::getFirstName, patient::setFirstName, "first name", changes);
        update(request.getLastName(), patient::getLastName, patient::setLastName, "last name", changes);
        update(request.getShortName(), patient::getShortName, patient::setShortName, "short name", changes);
        update(request.getEmail(), patient::getEmail, patient::setEmail, "email", changes);
        update(request.getPhone(), patient::getPhone, patient::setPhone, "phone", changes);
        update(request.getDateOfBirth(), patient::getDateOfBirth, patient::setDateOfBirth, "date of birth", changes);
        update(request.getGender(), patient::getGender, patient::setGender, "gender", changes);
        update(request.getNotes(), patient::getNotes, patient::setNotes, "notes", changes);
        update(request.getHandoverNotes(), patient::getHandoverNotes, patient::setHandoverNotes, "handover notes", changes);

        // 2. Custom Fields
        updateStatus(request.getIsActive(), patient, changes);
        updatePrice(request.getDefaultPrice(), patient, changes);
        updateColor(request.getCalendarColor(), patient, changes);

        String details = changes.isEmpty() ? "General info updated" : String.join(", ", changes);
        Patient updatedPatient = patientRepository.save(patient);
        auditService.log(AuditAction.UPDATE, "Patient", id, details);

        // Tip: Since it's @Transactional, Hibernate auto-saves changes when the method completes!
        return toResponseDto(updatedPatient);
    }

// --- HELPERS ---

    // Standard update: takes the getter and setter instead of evaluating the value upfront
    private <T> void update(T newValue, Supplier<T> getter, Consumer<T> setter, String label, List<String> changes) {
        if (newValue != null) {
            T oldValue = getter.get();
            if (!newValue.equals(oldValue)) {
                changes.add(label + ": " + oldValue + " → " + newValue);
                setter.accept(newValue);
            }
        }
    }

    private void updateStatus(Boolean newValue, Patient patient, List<String> changes) {
        if (newValue != null && !newValue.equals(patient.getIsActive())) {
            changes.add("status: " + (newValue ? "activated" : "deactivated"));
            patient.setIsActive(newValue);
        }
    }

    private void updatePrice(BigDecimal newValue, Patient patient, List<String> changes) {
        if (newValue != null) {
            BigDecimal oldPrice = patient.getDefaultPrice() != null ? patient.getDefaultPrice() : BigDecimal.ZERO;
            if (newValue.compareTo(oldPrice) != 0) {
                changes.add("price: " + oldPrice + " → " + newValue);
                patient.setDefaultPrice(newValue);
            }
        }
    }

    private void updateColor(Integer newValue, Patient patient, List<String> changes) {
        if (newValue != null && !newValue.equals(patient.getCalendarColor())) {
            changes.add("calendar color changed");
            patient.setCalendarColor(newValue);
        }
    }

    @Override
    @Transactional
    public void deactivatePatient(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        patient.setIsActive(false);
        patientRepository.save(patient);
        auditService.log(AuditAction.DELETE, "Patient", id, "Patient deactivated");
    }

    private void createAssignment(User therapist, Patient patient) {
        TherapistPatientAssignment assignment = TherapistPatientAssignment.builder()
                .therapist(therapist)
                .patient(patient)
                .assignedAt(LocalDateTime.now())
                .build();
        assignmentRepository.save(assignment);
        auditService.log(AuditAction.ASSIGN, "Patient", patient.getId(),
                "Assigned to therapist id: " + therapist.getId());
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
        Patient flaggedPatient = patientRepository.save(patient);
        auditService.log(AuditAction.FLAG, "Patient", id, note);
        return toResponseDto(flaggedPatient);
    }

    @Override
    @Transactional
    public PatientResponseDto clearFlag(Integer id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
        patient.setHasDebtFlag(false);
        patient.setDebtFlagNote(null);
        Patient clearedPatient = patientRepository.save(patient);
        auditService.log(AuditAction.CLEAR_FLAG, "Patient", id);
        return toResponseDto(clearedPatient);
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
        auditService.log(AuditAction.UNASSIGN, "Patient", patientId);
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
                //.sorted(java.util.Comparator.comparingInt(BirthdayPatientDto::getDaysUntil))
                .sorted((a, b) -> {
                    int daysA = a.getDaysUntil();
                    int daysB = b.getDaysUntil();

                    // both past — more recent first (less negative = closer to today)
                    if (daysA < 0 && daysB < 0) return Integer.compare(daysB, daysA);

                    // both future or today — sooner first
                    if (daysA >= 0 && daysB >= 0) return Integer.compare(daysA, daysB);

                    // today/future always comes before past
                    return daysA >= 0 ? -1 : 1;
                })
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

        List<Payment> writtenOff = paymentRepository
                .findByPatientIdAndStatus(patient.getId(), PaymentStatus.WRITTEN_OFF);

        BigDecimal writtenOffAmount = writtenOff.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime oldestWrittenOffDate = writtenOff.stream()
                .map(p -> p.getSession() != null
                        ? p.getSession().getScheduledAt() : null)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

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
                .writtenOffAmount(writtenOffAmount.compareTo(BigDecimal.ZERO) > 0
                        ? writtenOffAmount : null)
                .oldestWrittenOffDate(oldestWrittenOffDate)
                .calendarColor(patient.getCalendarColor())
                .build();
    }

    private <T> PageResponseDto<T> toPageResponse(Page<Patient> patientPage,
                                                  java.util.function.Function<Patient, T> mapper) {
        return PageResponseDto.<T>builder()
                .content(patientPage.getContent().stream().map(mapper).toList())
                .page(patientPage.getNumber())
                .size(patientPage.getSize())
                .totalElements(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .first(patientPage.isFirst())
                .last(patientPage.isLast())
                .build();
    }
}