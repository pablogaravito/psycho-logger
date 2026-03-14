package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    List<Patient> findByOrganizationIdAndIsActiveTrue(Integer orgId);
    List<Patient> findByOrganizationId(Integer orgId);
}
