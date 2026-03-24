package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.TherapistPatientAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TherapistPatientAssignmentRepository extends JpaRepository<TherapistPatientAssignment, Integer> {
    List<TherapistPatientAssignment> findByTherapistId(Integer therapistId);
    List<TherapistPatientAssignment> findByPatientId(Integer patientId);
    Optional<TherapistPatientAssignment> findByTherapistIdAndPatientIdAndUnassignedAtIsNull(Integer therapistId, Integer patientId);
    List<TherapistPatientAssignment> findByTherapistIdAndUnassignedAtIsNull(Integer therapistId);
    List<TherapistPatientAssignment> findByPatientIdOrderByAssignedAtDesc(Integer patientId);
    List<TherapistPatientAssignment> findByPatientOrganizationId(Integer orgId);
    long countByTherapistIdAndUnassignedAtIsNull(Integer therapistId);
}
