package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    @Override
    public PatientEntity getPatient(Long id) {
        Optional<PatientEntity> patient = patientRepository.findById(id);
        return unwrapPatient(patient, id);
    }

    @Override
    public List<PatientEntity> getPatients() {
        return (List<PatientEntity>) patientRepository.findAll();
    }

    @Override
    public Set<SessionEntity> getPatientSessions(Long id) {
        PatientEntity patientEntity = getPatient(id);
        return patientEntity.getSessions();
    }

    @Override
    public PatientEntity savePatient(PatientEntity patientEntity) {
        return patientRepository.save(patientEntity);
    }

    @Override
    public PatientEntity partialUpdatePatient(Long id, PatientEntity patientEntity) {
        patientEntity.setId(id);
        return patientRepository.findById(id).map(existingPatient -> {
            Optional.ofNullable(patientEntity.getFirstNames()).ifPresent(existingPatient::setFirstNames);
            Optional.ofNullable(patientEntity.getLastNames()).ifPresent(existingPatient::setLastNames);
            Optional.ofNullable(patientEntity.getShortName()).ifPresent(existingPatient::setShortName);
            Optional.ofNullable(patientEntity.getSex()).ifPresent(existingPatient::setSex);
            Optional.ofNullable(patientEntity.getBirthDate()).ifPresent(existingPatient::setBirthDate);
            Optional.ofNullable(patientEntity.getIsActive()).ifPresent(existingPatient::setIsActive);
            return patientRepository.save(existingPatient);
        }).orElseThrow(() -> new RuntimeException("Patient does not exist"));
    }



    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public boolean patientExists(Long id) {
        return patientRepository.existsById(id);
    }

    static PatientEntity unwrapPatient(Optional<PatientEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, PatientEntity.class);
    }
}
