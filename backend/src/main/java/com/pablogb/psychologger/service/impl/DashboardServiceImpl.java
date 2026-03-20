package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.response.MonthlySnapshotDto;
import com.pablogb.psychologger.dto.response.StatsResponseDto;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import com.pablogb.psychologger.repository.MonthlySnapshotRepository;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.PaymentRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;
    private final PaymentRepository paymentRepository;
    private final MonthlySnapshotRepository snapshotRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public StatsResponseDto getStats() {
        Integer orgId = securityUtils.getCurrentOrgId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime sevenDaysFromNow = now.plusDays(7);

        return StatsResponseDto.builder()
                .activePatients(patientRepository.countByOrganizationIdAndIsActiveTrue(orgId))
                .sessionsThisMonth(sessionRepository.countByOrganizationIdAndScheduledAtBetween(orgId, startOfMonth, now))
                .pendingPayments(paymentRepository.countByPatientOrganizationIdAndStatus (orgId, PaymentStatus.PENDING))
                .upcomingSessions(sessionRepository.countByOrganizationIdAndScheduledAtBetween(orgId, now, sevenDaysFromNow))
                .collectedThisMonth(paymentRepository
                        .sumAmountByUserIdAndStatusAndPaidAtBetween(
                                securityUtils.getCurrentUserId(),
                                PaymentStatus.PAID,
                                startOfMonth,
                                now))
                .birthdaysThisMonth(patientRepository
                        .countByOrganizationIdAndBirthdayThisMonth(orgId))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlySnapshotDto> getSnapshots() {
        return snapshotRepository
                .findByUserIdOrderByYearDescMonthDesc(securityUtils.getCurrentUserId())
                .stream()
                .map(s -> MonthlySnapshotDto.builder()
                        .year(s.getYear())
                        .month(s.getMonth())
                        .activePatients(s.getActivePatients())
                        .totalSessions(s.getTotalSessions())
                        .completedSessions(s.getCompletedSessions())
                        .totalCollected(s.getTotalCollected())
                        .totalPending(s.getTotalPending())
                        .build())
                .toList();
    }
}
