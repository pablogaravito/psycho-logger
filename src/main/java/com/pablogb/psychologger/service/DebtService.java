package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.view.PatientWithDebtContextDto;

import java.util.List;

public interface DebtService {
    List<PatientWithDebtContextDto> getPatientsWithDebt();
    void updateSessionPaidStatus(List<Long> sessionIds);
}
