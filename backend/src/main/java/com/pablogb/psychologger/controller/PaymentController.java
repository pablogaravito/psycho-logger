package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.request.PaymentRequestDto;
import com.pablogb.psychologger.dto.response.PaymentResponseDto;
import com.pablogb.psychologger.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByPatient(@PathVariable Integer patientId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPatient(patientId));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> updatePayment(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(paymentService.updatePayment(id, request));
    }
}
