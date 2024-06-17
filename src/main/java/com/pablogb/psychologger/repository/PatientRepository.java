package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.dto.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.domain.dto.PatientWithDebtContextDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PatientRepository extends CrudRepository<PatientEntity, Long> {

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

//    @Query(value = "SELECT * FROM PATIENT p WHERE " +
//            "((MONTH(p.birth_date) = MONTH(CURDATE()) AND DAY(p.birth_date) >= DAY(CURDATE())) OR " +
//            "(MONTH(p.birth_date) = MONTH(DATEADD('DAY', 30, CURDATE())) AND DAY(p.birth_date) <= DAY(DATEADD('DAY', 30, CURDATE())))) " +
//            "ORDER BY " +
//            "MONTH(p.birth_date), DAY(p.birth_date)",
//            nativeQuery = true)
//    List<PatientEntity> getPatientsWithUpcomingBirthdays();


    //    @Query(value = "SELECT * FROM PATIENT p WHERE " +
//            "((MONTH(p.birth_date) = MONTH(CURDATE()) AND DAY(p.birth_date) BETWEEN DAY(DATEADD('DAY', -5, CURDATE())) AND DAY(CURDATE())) OR " +
//            "(MONTH(p.birth_date) = MONTH(DATEADD('DAY', 30, CURDATE())) AND DAY(p.birth_date) <= DAY(DATEADD('DAY', 30, CURDATE()))) OR " +
//            "(MONTH(p.birth_date) = MONTH(DATEADD('DAY', -5, CURDATE())) AND DAY(p.birth_date) >= DAY(DATEADD('DAY', -5, CURDATE())))) " +
//            "ORDER BY " +
//            "CASE WHEN MONTH(p.birth_date) = MONTH(CURDATE()) THEN DAY(p.birth_date) ELSE 999 END, " +
//            "CASE WHEN MONTH(p.birth_date) = MONTH(DATEADD('DAY', 30, CURDATE())) THEN DAY(p.birth_date) ELSE 999 END, " +
//            "CASE WHEN MONTH(p.birth_date) = MONTH(DATEADD('DAY', -5, CURDATE())) THEN DAY(p.birth_date) ELSE 999 END",
//            nativeQuery = true)

//    SELECT
//  BRTHDATE AS BIRTHDAY
// ,FLOOR(DATEDIFF(dd,EMP.BRTHDATE,GETDATE()) / 365.25) AS AGE_NOW
// ,FLOOR(DATEDIFF(dd,EMP.BRTHDATE,GETDATE()+7) / 365.25) AS AGE_ONE_WEEK_FROM_NOW
//FROM
//  "Database name".dbo.EMPLOYEES EMP
//WHERE 1 = (FLOOR(DATEDIFF(dd,EMP.BRTHDATE,GETDATE()+7) / 365.25))
//          -
//          (FLOOR(DATEDIFF(dd,EMP.BRTHDATE,GETDATE()) / 365.25))

    @Query(value = "SELECT * FROM PATIENT p " +
            "WHERE DAYOFYEAR(p.birth_date) - DAYOFYEAR(CURDATE()) BETWEEN 0 AND 14 " +
            "OR " +
            "DAYOFYEAR( CONCAT(YEAR(CURDATE()),'-12-31') ) - ( DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) ) BETWEEN 0 AND 14 " +
            "OR " +
            "DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) BETWEEN 0 AND 7 " +
            "OR " +
            "(DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) ) - DAYOFYEAR( CONCAT(YEAR(CURDATE()), '-12-31')) BETWEEN 0 AND 7 " +
            "ORDER BY " +
            "CASE " +
            "WHEN DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) BETWEEN 0 AND 7 THEN 1 " +
            "WHEN (DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) ) - DAYOFYEAR( CONCAT(YEAR(CURDATE()), '-12-31')) BETWEEN 0 AND 7 THEN 2 " +
            "WHEN DAYOFYEAR(p.birth_date) - DAYOFYEAR(CURDATE()) BETWEEN 0 AND 14 THEN 2 " +
            "WHEN DAYOFYEAR( CONCAT(YEAR(CURDATE()),'-12-31') ) - ( DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) ) BETWEEN 0 AND 14 THEN 2 " +
            "END, " +
            "DAYOFYEAR(p.birth_date)", nativeQuery = true)
    List<PatientEntity> findPersonsWithUpcomingAndRecentBirthdays();

}
