package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.PaymentRequestDto;
import com.pablogb.psychologger.dto.response.PaymentResponseDto;
import com.pablogb.psychologger.exception.ResourceNotFoundException;
import com.pablogb.psychologger.model.entity.Patient;
import com.pablogb.psychologger.model.entity.Payment;
import com.pablogb.psychologger.model.entity.PaymentPlan;
import com.pablogb.psychologger.model.entity.Session;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.PaymentPlanRepository;
import com.pablogb.psychologger.repository.PaymentRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.PaymentService;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;
    private final PaymentPlanRepository paymentPlanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByPatient(Integer patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return paymentRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::toResponseDto)
                .toList();
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

        return toResponseDto(paymentRepository.save(payment));
    }

//    @Override
//    @Transactional
//    public PaymentResponseDto updatePayment(Integer id, PaymentRequestDto request) {
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
//
//        payment.setAmount(request.getAmount());
//        payment.setStatus(request.getStatus());
//        payment.setPaymentMethod(request.getPaymentMethod());
//        payment.setPaidAt(request.getPaidAt());
//        payment.setNotes(request.getNotes());
//
//        return toResponseDto(paymentRepository.save(payment));
//    }

    @Override
    @Transactional
    public PaymentResponseDto updatePayment(Integer id, PaymentRequestDto request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (request.getAmount() != null)
            payment.setAmount(request.getAmount());
        if (request.getStatus() != null) {
            payment.setStatus(request.getStatus());
            // auto flag patient when writing off
            if (request.getStatus() == PaymentStatus.WRITTEN_OFF) {
                Patient patient = payment.getPatient();
                patient.setHasDebtFlag(true);
                patient.setDebtFlagNote("Unpaid session written off on " +
                        LocalDate.now());
                patientRepository.save(patient);
            }
            // auto clear flag when paying off a written off payment
            if (request.getStatus() == PaymentStatus.PAID) {
                Patient patient = payment.getPatient();
                boolean stillHasWrittenOff = paymentRepository
                        .existsByPatientIdAndStatus(patient.getId(),
                                PaymentStatus.WRITTEN_OFF);
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

        return toResponseDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsBySession(Integer sessionId) {
        return paymentRepository.findBySessionId(sessionId)
                .stream()
                .map(this::toResponseDto)
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
