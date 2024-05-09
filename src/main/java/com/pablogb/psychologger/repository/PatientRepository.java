package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import org.springframework.data.repository.CrudRepository;

public interface PatientRepository extends CrudRepository<PatientEntity, Long> {
}
