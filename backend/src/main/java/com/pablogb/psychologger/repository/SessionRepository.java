package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByTherapistIdOrderByScheduledAtDesc(Integer therapistId);
    List<Session> findByPatientsIdOrderByScheduledAtDesc(Integer patientId);
    List<Session> findByOrganizationIdOrderByScheduledAtDesc(Integer orgId);
    long countByOrganizationIdAndScheduledAtBetween(Integer orgId, LocalDateTime start, LocalDateTime end);
}
