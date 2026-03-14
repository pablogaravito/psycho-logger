package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.PaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Integer> {
    List<PaymentPlan> findByPatientId(Integer patientId);
}
