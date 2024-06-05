package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PatientRepository extends CrudRepository<PatientEntity, Long> {

    @Query("select p from PatientEntity p join p.sessions s where s.id = :sessionId")
    Set<PatientEntity> getPatientsFromSession(Long sessionId);

    Set<PatientEntity> findByIsActiveTrue();
}
