package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.api.DebtSessionDto;
import com.pablogb.psychologger.dto.api.PatientWithDebtCountDto;
import com.pablogb.psychologger.dto.view.PatientWithDebtContextDto;

import java.util.List;

public interface DebtService {
    List<PatientWithDebtContextDto> getPatientsWithDebt();
    List<PatientWithDebtCountDto> getPatientsWithDebtApi();
    List<DebtSessionDto> getPatientDebt(Long id);
    void updateSessionPaidStatus(List<Long> sessionIds);
}
