package com.pablogb.psychologger.service;

import com.pablogb.psychologger.entity.Patient;
import com.pablogb.psychologger.entity.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


public interface PatientService {
    Patient getPatient(Long id);
    Patient saveStudent(Patient student);
    void deletePatient(Long id);
    List<Patient> getPatients();
    Set<Session> getPatientSessions(Long id);
}
