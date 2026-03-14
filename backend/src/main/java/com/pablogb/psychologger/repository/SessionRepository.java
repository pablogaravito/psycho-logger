package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findByTherapistIdOrderByScheduledAtDesc(Integer therapistId);
    List<Session> findByOrganizationIdOrderByScheduledAtDesc(Integer orgId);
}
