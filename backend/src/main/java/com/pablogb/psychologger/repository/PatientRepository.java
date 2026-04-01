package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    List<Patient> findByOrganizationIdAndIsActiveTrue(Integer orgId);
    List<Patient> findByOrganizationId(Integer orgId);
    long countByOrganizationIdAndIsActiveTrue(Integer orgId);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.organization.id = :orgId AND p.isActive = true AND MONTH(p.dateOfBirth) = MONTH(CURRENT_DATE) AND DAY(p.dateOfBirth) BETWEEN DAY(CURRENT_DATE) AND DAY(CURRENT_DATE) + 30")
    long countByOrganizationIdAndBirthdayThisMonth(@Param("orgId") Integer orgId);

    @Query("SELECT p FROM Patient p WHERE p.organization.id = :orgId AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Patient> searchByOrgAndName(@Param("orgId") Integer orgId, @Param("name") String name);

    Page<Patient> findByOrganizationId(Integer orgId, Pageable pageable);
    Page<Patient> findByOrganizationIdAndIsActiveTrue(Integer orgId, Pageable pageable);
    Page<Patient> findByOrganizationIdOrderByIsActiveDescLastNameAsc(
            Integer orgId, Pageable pageable);

    Page<Patient> findByOrganizationIdAndIsActiveTrueOrderByLastNameAsc(
            Integer orgId, Pageable pageable);
}

