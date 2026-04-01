package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.PaymentRequestDto;
import com.pablogb.psychologger.dto.request.PaymentUpdateDto;
import com.pablogb.psychologger.dto.response.PageResponseDto;
import com.pablogb.psychologger.dto.response.PatientDebtDto;
import com.pablogb.psychologger.dto.response.PaymentResponseDto;
import com.pablogb.psychologger.exception.ResourceNotFoundException;
import com.pablogb.psychologger.model.entity.*;
import com.pablogb.psychologger.model.enums.AuditAction;
import com.pablogb.psychologger.repository.*;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.AuditService;
import com.pablogb.psychologger.service.PaymentService;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pablogb.psychologger.dto.response.DebtPaymentDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;
    private final PaymentPlanRepository paymentPlanRepository;
    private final SecurityUtils securityUtils;
    private final TherapistPatientAssignmentRepository assignmentRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<PaymentResponseDto> getAllPayments(
            int page, int size, String status) {
        User currentUser = securityUtils.getCurrentUser();
        PageRequest pageable = PageRequest.of(page, size);

        Page<Payment> paymentPage;

        if (currentUser.getIsAdmin() && !currentUser.getIsTherapist()) {
            paymentPage = status != null && !status.isBlank()
                    ? paymentRepository.findByPatientOrganizationIdAndStatus(
                    currentUser.getOrganization().getId(),
                    PaymentStatus.valueOf(status), pageable)
                    : paymentRepository.findByPatientOrganizationId(
                    currentUser.getOrganization().getId(), pageable);
        } else {
            // get assigned patient IDs
            List<Integer> assignedPatientIds = assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNull(currentUser.getId())
                    .stream()
                    .map(a -> a.getPatient().getId())
                    .toList();

            paymentPage = status != null && !status.isBlank()
                    ? paymentRepository.findByPatientIdInAndStatusOrderByCreatedAtDesc(
                    assignedPatientIds,
                    PaymentStatus.valueOf(status),
                    pageable)
                    : paymentRepository.findByPatientIdInOrderByCreatedAtDesc(
                    assignedPatientIds, pageable);
        }

        return PageResponseDto.<PaymentResponseDto>builder()
                .content(paymentPage.getContent().stream()
                        .map(this::toResponseDto)
                        .toList())
                .page(paymentPage.getNumber())
                .size(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .first(paymentPage.isFirst())
                .last(paymentPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<PaymentResponseDto> getPaymentsByPatient(
            Integer patientId, int page, int size) {
        User currentUser = securityUtils.getCurrentUser();

        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException(
                    "Patient not found with id: " + patientId);
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage;

        if (currentUser.getIsAdmin()) {
            paymentPage = paymentRepository
                    .findByPatientIdOrderByCreatedAtDesc(patientId, pageable);
        } else {
            boolean isAssigned = assignmentRepository
                    .findByTherapistIdAndUnassignedAtIsNull(currentUser.getId())
                    .stream()
                    .anyMatch(a -> a.getPatient().getId().equals(patientId));

            if (!isAssigned) {
                throw new ResourceNotFoundException(
                        "Access denied — patient not assigned to you");
            }
            paymentPage = paymentRepository
                    .findByPatientIdOrderByCreatedAtDesc(patientId, pageable);
        }

        return PageResponseDto.<PaymentResponseDto>builder()
                .content(paymentPage.getContent().stream()
                        .map(this::toResponseDto)
                        .toList())
                .page(paymentPage.getNumber())
                .size(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .first(paymentPage.isFirst())
                .last(paymentPage.isLast())
                .build();
    }



    @Override
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Session session = request.getSessionId() != null
                ? sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()))
                : null;

        PaymentPlan plan = request.getPaymentPlanId() != null
                ? paymentPlanRepository.findById(request.getPaymentPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment plan not found with id: " + request.getPaymentPlanId()))
                : null;

        Payment payment = Payment.builder()
                .patient(patient)
                .session(session)
                .paymentPlan(plan)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(request.getStatus())
                .paymentMethod(request.getPaymentMethod())
                .paidAt(request.getPaidAt())
                .notes(request.getNotes())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        auditService.log(AuditAction.CREATE, "Payment", savedPayment.getId());
        return toResponseDto(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponseDto updatePayment(Integer id, PaymentUpdateDto request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + id));

        // apply changes first
        if (request.getAmount() != null)
            payment.setAmount(request.getAmount());

        if (request.getStatus() != null) {
            payment.setStatus(request.getStatus());
            if (request.getStatus() == PaymentStatus.WRITTEN_OFF) {
                Patient patient = payment.getPatient();
                patient.setHasDebtFlag(true);
                patient.setDebtFlagNote("Unpaid session written off on "
                        + LocalDate.now());
                patientRepository.save(patient);
            }
            if (request.getStatus() == PaymentStatus.PAID) {
                Patient patient = payment.getPatient();
                boolean stillHasWrittenOff = paymentRepository
                        .existsByPatientIdAndStatus(
                                patient.getId(), PaymentStatus.WRITTEN_OFF);
                if (!stillHasWrittenOff) {
                    patient.setHasDebtFlag(false);
                    patientRepository.save(patient);
                }
            }
        }

        if (request.getPaymentMethod() != null)
            payment.setPaymentMethod(request.getPaymentMethod());
        if (request.getPaidAt() != null)
            payment.setPaidAt(request.getPaidAt());
        if (request.getNotes() != null)
            payment.setNotes(request.getNotes());

        // save
        Payment saved = paymentRepository.save(payment);

        // log after save
        if (request.getStatus() != null) {
            if (request.getStatus() == PaymentStatus.PAID) {
                auditService.log(AuditAction.MARK_PAID, "Payment", id,
                        "Amount: " + saved.getAmount() + " " + saved.getCurrency());
            } else if (request.getStatus() == PaymentStatus.WRITTEN_OFF) {
                auditService.log(AuditAction.WRITE_OFF, "Payment", id,
                        "Amount: " + saved.getAmount() + " " + saved.getCurrency());
            } else if (request.getStatus() == PaymentStatus.PENDING) {
                auditService.log(AuditAction.REACTIVATE, "Payment", id);
            } else {
                auditService.log(AuditAction.UPDATE, "Payment", id);
            }
        } else {
            // status didn't change, just a general update
            auditService.log(AuditAction.UPDATE, "Payment", id);
        }

        return toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsBySession(Integer sessionId) {
        return paymentRepository.findBySessionId(sessionId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDebtDto> getDebts() {

        User currentUser = securityUtils.getCurrentUser();

        List<Payment> pending;

        if (currentUser.getIsAdmin() && !currentUser.getIsTherapist()) {
            // pure admin sees all org debts
            pending = paymentRepository
                    .findByPatientOrganizationIdAndStatus(
                            currentUser.getOrganization().getId(),
                            PaymentStatus.PENDING);
        } else {
            // therapist sees only their patients' debts
            pending = paymentRepository
                    .findBySessionTherapistIdAndStatus(
                            currentUser.getId(),
                            PaymentStatus.PENDING);
        }

        // group by patient
        return pending.stream()
                .collect(java.util.stream.Collectors.groupingBy(Payment::getPatient))
                .entrySet().stream()
                .map(entry -> {
                    Patient patient = entry.getKey();
                    List<Payment> payments = entry.getValue();

                    List<DebtPaymentDto> paymentDtos = payments.stream()
                            .map(p -> DebtPaymentDto.builder()
                                    .paymentId(p.getId())
                                    .sessionId(p.getSession() != null
                                            ? p.getSession().getId() : null)
                                    .sessionDate(p.getSession() != null
                                            ? p.getSession().getScheduledAt() : null)
                                    .amount(p.getAmount())
                                    .currency(p.getCurrency())
                                    .build())
                            .sorted(java.util.Comparator.comparing(
                                    DebtPaymentDto::getSessionDate,
                                    java.util.Comparator.nullsLast(
                                            java.util.Comparator.reverseOrder())))
                            .toList();

                    BigDecimal total = payments.stream()
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return PatientDebtDto.builder()
                            .patientId(patient.getId())
                            .patientName(patient.getFirstName() + " "
                                    + patient.getLastName())
                            .shortName(patient.getShortName())
                            .hasDebtFlag(patient.getHasDebtFlag())
                            .isActive(patient.getIsActive())
                            .pendingCount(payments.size())
                            .totalPending(total)
                            .pendingPayments(paymentDtos)
                            .build();
                })
                // active patients first, then by total pending descending
                .sorted(java.util.Comparator
                        .comparing(PatientDebtDto::getIsActive).reversed()
                        .thenComparing(java.util.Comparator
                                .comparing(PatientDebtDto::getTotalPending).reversed()))
                .toList();
    }

    private PaymentResponseDto toResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .patientId(payment.getPatient().getId())
                .patientName(payment.getPatient().getFirstName() + " " + payment.getPatient().getLastName())
                .sessionId(payment.getSession() != null ? payment.getSession().getId() : null)
                .paymentPlanId(payment.getPaymentPlan() != null ? payment.getPaymentPlan().getId() : null)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .paidAt(payment.getPaidAt())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
