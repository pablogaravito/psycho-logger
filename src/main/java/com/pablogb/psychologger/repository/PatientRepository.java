package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.entity.Patient;
import org.springframework.data.repository.CrudRepository;

public interface PatientRepository extends CrudRepository<Patient, Long> {
}
