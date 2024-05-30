package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PatientRepository extends CrudRepository<PatientEntity, Long> {
    @Query("select s from SessionEntity s join s.patients p where p.id = :patientId")
    Set<SessionEntity> getSessionsFromPatient(Long patientId);

    Set<PatientEntity> findByIsActiveTrue();
}
