package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.PaymentPlanRequestDto;
import com.pablogb.psychologger.dto.response.PaymentPlanResponseDto;
import java.util.List;

public interface PaymentPlanService {
    List<PaymentPlanResponseDto> getPaymentPlansByPatient(Integer patientId);
    PaymentPlanResponseDto createPaymentPlan(PaymentPlanRequestDto request);
    PaymentPlanResponseDto updatePaymentPlan(Integer id, PaymentPlanRequestDto request);
}
