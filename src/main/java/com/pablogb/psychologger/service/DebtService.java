package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.dto.PatientWithDebtContextDto;

import java.util.List;

public interface DebtService {
    List<PatientWithDebtContextDto> getPatientsWithDebt();
    void updateSessionPaidStatus(List<Long> sessionIds);
}
