package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.PaymentRequestDto;
import com.pablogb.psychologger.dto.response.PaymentResponseDto;
import com.pablogb.psychologger.model.entity.Patient;
import com.pablogb.psychologger.model.entity.Payment;
import com.pablogb.psychologger.model.entity.PaymentPlan;
import com.pablogb.psychologger.model.entity.Session;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.PaymentPlanRepository;
import com.pablogb.psychologger.repository.PaymentRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByPatient(Integer patientId) {
        return paymentRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + request.getPatientId()));

        Session session = request.getSessionId() != null
                ? sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + request.getSessionId()))
                : null;

        PaymentPlan plan = request.getPaymentPlanId() != null
                ? paymentPlanRepository.findById(request.getPaymentPlanId())
                .orElseThrow(() -> new RuntimeException("Payment plan not found with id: " + request.getPaymentPlanId()))
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

    @Override
    @Transactional
    public PaymentResponseDto updatePayment(Integer id, PaymentRequestDto request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        payment.setAmount(request.getAmount());
        payment.setStatus(request.getStatus());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaidAt(request.getPaidAt());
        payment.setNotes(request.getNotes());

        return toResponseDto(paymentRepository.save(payment));
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
