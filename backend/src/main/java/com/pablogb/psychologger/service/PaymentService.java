package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.PaymentRequestDto;
import com.pablogb.psychologger.dto.response.PaymentResponseDto;
import java.util.List;

public interface PaymentService {
    List<PaymentResponseDto> getAllPayments();
    List<PaymentResponseDto> getPaymentsByPatient(Integer patientId);
    PaymentResponseDto createPayment(PaymentRequestDto request);
    PaymentResponseDto updatePayment(Integer id, PaymentRequestDto request);
}
