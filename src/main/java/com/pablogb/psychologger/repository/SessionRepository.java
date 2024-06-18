package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    @Query("select s from SessionEntity s join s.patients p where p.id = :patientId")
    Set<SessionEntity> getSessionsFromPatient(Long patientId);
}
