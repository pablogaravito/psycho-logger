package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.dto.api.PatientWithDebtCountDto;
import com.pablogb.psychologger.dto.view.DebtSessionViewDto;
import com.pablogb.psychologger.dto.view.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    @Query("SELECT p FROM PatientEntity p JOIN p.sessions s WHERE s.id = :sessionId")
    List<PatientEntity> getPatientsFromSession(@Param("sessionId") Long sessionId);

    List<PatientEntity> findByIsActiveTrue();

    Page<PatientEntity> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM PatientEntity p WHERE LOWER(p.shortName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.firstNames) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.lastNames) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PatientEntity> searchPatientByName(@Param("name") String name);

    @Query("SELECT new com.pablogb.psychologger.dto.api.PatientWithDebtCountDto(p.id, p.shortName, COUNT(s)) " +
            "FROM SessionEntity s JOIN s.patients p " +
            "WHERE s.isPaid = false " +
            "GROUP BY p.id, p.shortName " +
            "ORDER BY COUNT(s) DESC")
    List<PatientWithDebtCountDto> getPatientsWithDebt();

    @Query("SELECT new com.pablogb.psychologger.dto.view.DebtSessionViewDto(s.id, s.themes, s.sessionDate, s.isPaid) " +
            "FROM SessionEntity s JOIN s.patients p " +
            "WHERE p.id = :patientId AND s.isPaid = false " +
            "ORDER BY s.sessionDate DESC")
    List<DebtSessionViewDto> getPatientDebt(@Param("patientId") Long patientId);

    @Query(name = "PatientEntity.findPersonsWithUpcomingAndRecentBirthdays", nativeQuery = true)
    List<PatientWithBirthdayContextDto> findPersonsWithUpcomingAndRecentBirthdays();
}
