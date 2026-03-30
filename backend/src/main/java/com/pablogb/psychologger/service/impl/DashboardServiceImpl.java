package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.response.MonthlySnapshotDto;
import com.pablogb.psychologger.dto.response.StatsResponseDto;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.model.entity.UserSettings;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import com.pablogb.psychologger.repository.*;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.DashboardService;
import com.pablogb.psychologger.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final PatientService patientService;
    private final TherapistPatientAssignmentRepository assignmentRepository;
    private final UserSettingsRepository userSettingsRepository;

    @Override
    @Transactional(readOnly = true)
    public StatsResponseDto getStats() {
        User currentUser = securityUtils.getCurrentUser();
        Integer orgId = currentUser.getOrganization().getId();
        Integer userId = currentUser.getId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime sevenDaysFromNow = now.plusDays(7);

        long activePatients;
        long sessionsThisMonth;
        long upcomingSessions;
        BigDecimal collectedThisMonth;
        long pendingPayments;

        if (currentUser.getIsAdmin() && !currentUser.getIsTherapist()) {
            // pure admin — org wide stats
            activePatients = patientRepository
                    .countByOrganizationIdAndIsActiveTrue(orgId);
            sessionsThisMonth = sessionRepository
                    .countByOrganizationIdAndScheduledAtBetween(
                            orgId, startOfMonth, now);
            upcomingSessions = sessionRepository
                    .countByOrganizationIdAndScheduledAtBetween(
                            orgId, now, sevenDaysFromNow);
            collectedThisMonth = paymentRepository
                    .sumAmountByOrgIdAndStatusAndPaidAtBetween(
                            orgId, PaymentStatus.PAID, startOfMonth, now);
            pendingPayments = paymentRepository
                    .countByPatientOrganizationIdAndStatus(
                            orgId, PaymentStatus.PENDING);
        } else {
            // therapist — own stats only
            activePatients = assignmentRepository
                    .countByTherapistIdAndUnassignedAtIsNull(userId);
            sessionsThisMonth = sessionRepository
                    .countByTherapistIdAndScheduledAtBetween(
                            userId, startOfMonth, now);
            upcomingSessions = sessionRepository
                    .countByTherapistIdAndScheduledAtBetween(
                            userId, now, sevenDaysFromNow);
            collectedThisMonth = paymentRepository
                    .sumAmountByUserIdAndStatusAndPaidAtBetween(
                            userId, PaymentStatus.PAID, startOfMonth, now);
            pendingPayments = paymentRepository
                    .countBySessionTherapistIdAndStatus(
                            userId, PaymentStatus.PENDING);
        }

        boolean includeInactive = userSettingsRepository
                .findByUserId(securityUtils.getCurrentUserId())
                .map(UserSettings::getShowInactiveBirthdays)
                .orElse(false);

        long birthdaysThisWeek = patientService
                .getUpcomingBirthdays(includeInactive)
                .stream()
                .filter(b -> b.getDaysUntil() >= 0 && b.getDaysUntil() <= 7)
                .count();

        return StatsResponseDto.builder()
                .activePatients(activePatients)
                .sessionsThisMonth(sessionsThisMonth)
                .upcomingSessions(upcomingSessions)
                .collectedThisMonth(collectedThisMonth != null
                        ? collectedThisMonth : BigDecimal.ZERO)
                .pendingPayments(pendingPayments)
                .birthdaysThisMonth(birthdaysThisWeek)
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
