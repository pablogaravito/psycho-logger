package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Payment;
import com.pablogb.psychologger.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByPatientId(Integer patientId);
    List<Payment> findBySessionId(Integer sessionId);
    long countByPatientOrganizationIdAndStatus(Integer orgId, PaymentStatus status);
}
