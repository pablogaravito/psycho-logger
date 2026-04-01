package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Payment;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAllByOrderByCreatedAtDesc();
    List<Payment> findByPatientIdOrderByCreatedAtDesc(Integer patientId);
    List<Payment> findByPatientId(Integer patientId);
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

    List<Payment> findBySessionTherapistId(Integer therapistId);
    List<Payment> findByPatientOrganizationId(Integer orgId);
    List<Payment> findBySessionTherapistIdAndStatus(
            Integer therapistId, PaymentStatus status);

    long countBySessionTherapistIdAndStatus(
            Integer therapistId, PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.patient.organization.id = :orgId " +
            "AND p.status = :status AND p.paidAt BETWEEN :start AND :end")
    BigDecimal sumAmountByOrgIdAndStatusAndPaidAtBetween(
            @Param("orgId") Integer orgId,
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Payment> findByPatientIdAndStatus(Integer patientId, PaymentStatus status);

    Page<Payment> findByPatientOrganizationId(Integer orgId, Pageable pageable);
    Page<Payment> findByPatientOrganizationIdAndStatus(
            Integer orgId, PaymentStatus status, Pageable pageable);
    Page<Payment> findBySessionTherapistId(Integer therapistId, Pageable pageable);
    Page<Payment> findBySessionTherapistIdAndStatus(
            Integer therapistId, PaymentStatus status, Pageable pageable);
    Page<Payment> findByPatientIdOrderByCreatedAtDesc(
            Integer patientId, Pageable pageable);

    Page<Payment> findByPatientIdInOrderByCreatedAtDesc(List<Integer> patientIds, Pageable pageable);

    Page<Payment> findByPatientIdInAndStatusOrderByCreatedAtDesc(List<Integer> patientIds, PaymentStatus status, Pageable pageable);
}
