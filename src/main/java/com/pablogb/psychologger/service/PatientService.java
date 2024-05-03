package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.entity.Patient;
import com.pablogb.psychologger.domain.entity.Session;

import java.util.List;
import java.util.Set;


public interface PatientService {
    Patient getPatient(Long id);
    Patient savePatient(Patient patient);
    Patient updatePatient(Long id, Patient patient);
    void deletePatient(Long id);
    List<Patient> getPatients();
    Set<Session> getPatientSessions(Long id);
}
