package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Payment;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAllByOrderByCreatedAtDesc();
    List<Payment> findByPatientIdOrderByCreatedAtDesc(Integer patientId);
    //List<Payment> findByPatientId(Integer patientId);
    List<Payment> findBySessionId(Integer sessionId);
    long countByPatientOrganizationIdAndStatus(Integer orgId, PaymentStatus status);
    void deleteBySessionId(Integer sessionId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.session.therapist.id = :userId AND p.status = :status AND p.paidAt BETWEEN :start AND :end")
    BigDecimal sumAmountByUserIdAndStatusAndPaidAtBetween(
            @Param("userId") Integer userId,
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.session.therapist.id = :userId AND p.status = :status")
    BigDecimal sumAmountByUserIdAndStatus(
            @Param("userId") Integer userId,
            @Param("status") PaymentStatus status);

    boolean existsByPatientIdAndStatus(Integer patientId, PaymentStatus status);

    List<Payment> findByPatientOrganizationIdAndStatus(Integer orgId, PaymentStatus status);
}
