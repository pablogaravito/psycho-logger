package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.request.PaymentRequestDto;
import com.pablogb.psychologger.dto.request.PaymentUpdateDto;
import com.pablogb.psychologger.dto.response.PageResponseDto;
import com.pablogb.psychologger.dto.response.PatientDebtDto;
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
    public ResponseEntity<PageResponseDto<PaymentResponseDto>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(paymentService.getAllPayments(page, size, status));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<PageResponseDto<PaymentResponseDto>> getPaymentsByPatient(
            @PathVariable Integer patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(paymentService.getPaymentsByPatient(
                patientId, page, size));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> updatePayment(
            @PathVariable Integer id,
            @RequestBody PaymentUpdateDto request) {
        return ResponseEntity.ok(paymentService.updatePayment(id, request));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsBySession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(paymentService.getPaymentsBySession(sessionId));
    }

    @GetMapping("/debts")
    public ResponseEntity<List<PatientDebtDto>> getDebts() {
        return ResponseEntity.ok(paymentService.getDebts());
    }
}
