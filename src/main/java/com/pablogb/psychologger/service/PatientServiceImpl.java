package com.pablogb.psychologger.service;

import com.pablogb.psychologger.domain.entity.Patient;
import com.pablogb.psychologger.domain.entity.Session;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepository;
    @Override
    public Patient getPatient(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        return unwrapPatient(patient, id);
    }

    @Override
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public Patient updatePatient(Long id, Patient patient) {
        patient.setId(id);
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public List<Patient> getPatients() {
        return (List<Patient>) patientRepository.findAll();
    }

    @Override
    public Set<Session> getPatientSessions(Long id) {
        Patient patient = getPatient(id);
        return patient.getSessions();
    }

    static Patient unwrapPatient(Optional<Patient> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, Patient.class);
    }
}
