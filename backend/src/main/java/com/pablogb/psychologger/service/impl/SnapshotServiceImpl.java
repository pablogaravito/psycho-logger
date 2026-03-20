package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.model.entity.MonthlySnapshot;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import com.pablogb.psychologger.model.enums.SessionStatus;
import com.pablogb.psychologger.repository.*;
import com.pablogb.psychologger.service.SnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    private final MonthlySnapshotRepository snapshotRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void takeSnapshotForMonth(Integer userId, Integer year, Integer month) {
        // skip if already exists
        if (snapshotRepository.existsByUserIdAndYearAndMonth(userId, year, month)) {
            log.info("Snapshot already exists for user {} - {}/{}", userId, year, month);
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        // active patients at end of month
        long activePatients = patientRepository
                .countByOrganizationIdAndIsActiveTrue(user.getOrganization().getId());

        // sessions in that month
        long totalSessions = sessionRepository
                .countByOrganizationIdAndScheduledAtBetween(
                        user.getOrganization().getId(), start, end);

        long completedSessions = sessionRepository
                .countByTherapistIdAndStatusAndScheduledAtBetween(
                        userId, SessionStatus.COMPLETED, start, end);

        // payments in that month
        BigDecimal totalCollected = paymentRepository
                .sumAmountByUserIdAndStatusAndPaidAtBetween(
                        userId, PaymentStatus.PAID, start, end);

        BigDecimal totalPending = paymentRepository
                .sumAmountByUserIdAndStatus(userId, PaymentStatus.PENDING);

        BigDecimal totalWrittenOff = paymentRepository
                .sumAmountByUserIdAndStatusAndPaidAtBetween(
                        userId, PaymentStatus.WRITTEN_OFF, start, end);

        MonthlySnapshot snapshot = MonthlySnapshot.builder()
                .organization(user.getOrganization())
                .user(user)
                .year(year)
                .month(month)
                .activePatients((int) activePatients)
                .totalSessions((int) totalSessions)
                .completedSessions((int) completedSessions)
                .totalCollected(totalCollected != null ? totalCollected : BigDecimal.ZERO)
                .totalPending(totalPending != null ? totalPending : BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        snapshotRepository.save(snapshot);
        log.info("Snapshot saved for user {} - {}/{}", userId, year, month);
    }

    @Override
    @Transactional
    public void takeCurrentMonthSnapshot() {
        List<User> allUsers = userRepository.findAll();
        YearMonth last = YearMonth.now().minusMonths(1);
        for (User user : allUsers) {
            takeSnapshotForMonth(user.getId(), last.getYear(), last.getMonthValue());
        }
    }

    @Override
    @Transactional
    public void catchUpMissingSnapshots() {
        List<User> allUsers = userRepository.findAll();
        YearMonth current = YearMonth.now();

        for (User user : allUsers) {
            // find earliest session for this user to know where to start
            LocalDateTime earliest = sessionRepository
                    .findEarliestScheduledAtByTherapistId(user.getId());

            if (earliest == null) continue;

            YearMonth start = YearMonth.from(earliest.toLocalDate());
            YearMonth cursor = start;

            // go month by month up to last month
            while (!cursor.equals(current)) {
                if (!snapshotRepository.existsByUserIdAndYearAndMonth(
                        user.getId(), cursor.getYear(), cursor.getMonthValue())) {
                    takeSnapshotForMonth(user.getId(), cursor.getYear(), cursor.getMonthValue());
                }
                cursor = cursor.plusMonths(1);
            }
        }
        log.info("Catch-up snapshots completed");
    }
}
