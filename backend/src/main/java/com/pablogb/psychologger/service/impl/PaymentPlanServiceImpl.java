package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.PaymentPlanRequestDto;
import com.pablogb.psychologger.dto.response.PaymentPlanResponseDto;
import com.pablogb.psychologger.model.entity.Patient;
import com.pablogb.psychologger.model.entity.PaymentPlan;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.PaymentPlanRepository;
import com.pablogb.psychologger.repository.UserRepository;
import com.pablogb.psychologger.service.PaymentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentPlanServiceImpl implements PaymentPlanService {

    private final PaymentPlanRepository paymentPlanRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentPlanResponseDto> getPaymentPlansByPatient(Integer patientId) {
        return paymentPlanRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentPlanResponseDto createPaymentPlan(PaymentPlanRequestDto request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + request.getPatientId()));
        User therapist = userRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        PaymentPlan plan = PaymentPlan.builder()
                .patient(patient)
                .therapist(therapist)
                .totalSessions(request.getTotalSessions())
                .sessionsUsed(0)
                .pricePerSession(request.getPricePerSession())
                .totalAmount(request.getTotalAmount())
                .paidAt(request.getPaidAt())
                .build();

        return toResponseDto(paymentPlanRepository.save(plan));
    }

    @Override
    @Transactional
    public PaymentPlanResponseDto updatePaymentPlan(Integer id, PaymentPlanRequestDto request) {
        PaymentPlan plan = paymentPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment plan not found with id: " + id));

        plan.setTotalSessions(request.getTotalSessions());
        plan.setPricePerSession(request.getPricePerSession());
        plan.setTotalAmount(request.getTotalAmount());
        plan.setPaidAt(request.getPaidAt());

        return toResponseDto(paymentPlanRepository.save(plan));
    }

    private PaymentPlanResponseDto toResponseDto(PaymentPlan plan) {
        return PaymentPlanResponseDto.builder()
                .id(plan.getId())
                .patientId(plan.getPatient().getId())
                .patientName(plan.getPatient().getFirstName() + " " + plan.getPatient().getLastName())
                .totalSessions(plan.getTotalSessions())
                .sessionsUsed(plan.getSessionsUsed())
                .pricePerSession(plan.getPricePerSession())
                .totalAmount(plan.getTotalAmount())
                .status(plan.getStatus())
                .paidAt(plan.getPaidAt())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
