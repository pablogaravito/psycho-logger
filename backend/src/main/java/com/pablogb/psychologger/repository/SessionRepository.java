package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Session;
import com.pablogb.psychologger.model.enums.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByTherapistIdOrderByScheduledAtDesc(Integer therapistId);
    List<Session> findByPatientsIdOrderByScheduledAtDesc(Integer patientId);
    List<Session> findByOrganizationIdOrderByScheduledAtDesc(Integer orgId);
    long countByOrganizationIdAndScheduledAtBetween(Integer orgId, LocalDateTime start, LocalDateTime end);
    long countByTherapistIdAndStatusAndScheduledAtBetween(
            Integer therapistId, SessionStatus status,
            LocalDateTime start, LocalDateTime end);

    @Query("SELECT MIN(s.scheduledAt) FROM Session s WHERE s.therapist.id = :therapistId")
    LocalDateTime findEarliestScheduledAtByTherapistId(@Param("therapistId") Integer therapistId);

    long countByTherapistIdAndScheduledAtBetween(
            Integer therapistId, LocalDateTime start, LocalDateTime end);

    Page<Session> findByTherapistIdOrderByScheduledAtDesc(
            Integer therapistId, Pageable pageable);

    Page<Session> findByPatientsIdAndTherapistIdOrderByScheduledAtDesc(
            Integer patientId, Integer therapistId, Pageable pageable);
}
