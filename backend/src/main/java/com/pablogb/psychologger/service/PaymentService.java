package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.PaymentRequestDto;
import com.pablogb.psychologger.dto.request.PaymentUpdateDto;
import com.pablogb.psychologger.dto.response.PageResponseDto;
import com.pablogb.psychologger.dto.response.PatientDebtDto;
import com.pablogb.psychologger.dto.response.PaymentResponseDto;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto request);
    PaymentResponseDto updatePayment(Integer id, PaymentUpdateDto request);
    List<PaymentResponseDto> getPaymentsBySession(Integer sessionId);
    List<PatientDebtDto> getDebts();
    PageResponseDto<PaymentResponseDto> getAllPayments(int page, int size, String status);
    PageResponseDto<PaymentResponseDto> getPaymentsByPatient(Integer patientId, int page, int size);
}
