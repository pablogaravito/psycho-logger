package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.dto.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.domain.dto.PatientWithDebtContextDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    @Query("SELECT p FROM PatientEntity p JOIN p.sessions s WHERE s.id = :sessionId")
    Set<PatientEntity> getPatientsFromSession(@Param("sessionId") Long sessionId);

    Set<PatientEntity> findByIsActiveTrue();

    List<PatientEntity> findByShortNameContainingIgnoreCaseOrFirstNamesContainingIgnoreCaseOrLastNamesContainingIgnoreCase(String shortName, String firstNames, String lastNames);

    @Query("SELECT new com.pablogb.psychologger.domain.dto.PatientWithDebtContextDto(p.id, p.shortName, COUNT(s)) " +
            "FROM SessionEntity s JOIN s.patients p " +
            "WHERE s.isPaid = false " +
            "GROUP BY p.id, p.shortName " +
            "ORDER BY COUNT(s) DESC")
    List<PatientWithDebtContextDto> getPatientsWithDebt();

    @Query(name = "PatientEntity.findPersonsWithUpcomingAndRecentBirthdays", nativeQuery = true)
    List<PatientWithBirthdayContextDto> findPersonsWithUpcomingAndRecentBirthdays();
}
