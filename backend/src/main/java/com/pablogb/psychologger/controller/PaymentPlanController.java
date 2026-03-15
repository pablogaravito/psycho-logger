package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.request.PaymentPlanRequestDto;
import com.pablogb.psychologger.dto.response.PaymentPlanResponseDto;
import com.pablogb.psychologger.service.PaymentPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-plans")
@RequiredArgsConstructor
public class PaymentPlanController {

    private final PaymentPlanService paymentPlanService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PaymentPlanResponseDto>> getPaymentPlansByPatient(@PathVariable Integer patientId) {
        return ResponseEntity.ok(paymentPlanService.getPaymentPlansByPatient(patientId));
    }

    @PostMapping
    public ResponseEntity<PaymentPlanResponseDto> createPaymentPlan(@Valid @RequestBody PaymentPlanRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentPlanService.createPaymentPlan(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentPlanResponseDto> updatePaymentPlan(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentPlanRequestDto request) {
        return ResponseEntity.ok(paymentPlanService.updatePaymentPlan(id, request));
    }
}