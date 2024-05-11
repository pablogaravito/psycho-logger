package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.dao.PatchPatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;

import java.util.List;
import java.util.Set;


public interface PatientService {
    PatientEntity getPatient(Long id);
    Set<PatientEntity> getPatients();
    Set<SessionEntity> getPatientSessions(Long id);
    PatientEntity savePatient(PatientEntity patientEntity);
    PatientEntity partialUpdatePatient(PatchPatientDto patchPatientDto);
    void deletePatient(Long id);
    boolean patientExists(Long id);

}
