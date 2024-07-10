package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.view.PatientWithDebtContextDto;
import com.pablogb.psychologger.dto.api.PatientWithDebtCountDto;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.DebtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;

    @Override
    public List<PatientWithDebtContextDto> getPatientsWithDebt() {
        List<PatientWithDebtCountDto> patientsWithDebt = patientRepository.getPatientsWithDebt();
        return patientsWithDebt.stream().map(p -> PatientWithDebtContextDto.create(p, patientRepository.getPatientDebt(p.getId()))).toList();
    }

    @Override
    @Transactional
    public void updateSessionPaidStatus(List<Long> sessionIds) {
        sessionIds.forEach(id -> {
            SessionEntity session = sessionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(id, SessionEntity.class));
            session.setIsPaid(true);
            sessionRepository.save(session);
        });
    }
}
