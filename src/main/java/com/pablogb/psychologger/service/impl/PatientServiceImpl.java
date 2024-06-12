package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.controller.gui.view.PatientShort;
import com.pablogb.psychologger.domain.dto.PatchPatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import com.pablogb.psychologger.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;

    @Override
    public PatientEntity getPatient(Long id) {
        Optional<PatientEntity> patient = patientRepository.findById(id);
        return unwrapPatient(patient, id);
    }

    @Override
    public Set<PatientEntity> getPatients() {
        Set<PatientEntity> patients = new HashSet<>();
        Iterable<PatientEntity> patientEntities = patientRepository.findAll();
        patientEntities.forEach(patients::add);
        return patients;
    }

    @Override
    public Set<PatientEntity> getActivePatients() {
        return patientRepository.findByIsActiveTrue();
    }

    @Override
    public Set<SessionEntity> getPatientSessions(Long id) {
        return sessionRepository.getSessionsFromPatient(id);
    }

    @Override
    public PatientEntity savePatient(PatientEntity patientEntity) {
        return patientRepository.save(patientEntity);
    }

    @Override
    public PatientEntity partialUpdatePatient(PatchPatientDto patchPatientDto) {
        return patientRepository.findById(patchPatientDto.getId()).map(existingPatient -> {
            Optional.ofNullable(patchPatientDto.getFirstNames()).ifPresent(existingPatient::setFirstNames);
            Optional.ofNullable(patchPatientDto.getLastNames()).ifPresent(existingPatient::setLastNames);
            Optional.ofNullable(patchPatientDto.getShortName()).ifPresent(existingPatient::setShortName);
            Optional.ofNullable(patchPatientDto.getSex()).ifPresent(existingPatient::setSex);
            Optional.ofNullable(patchPatientDto.getBirthDate()).ifPresent(existingPatient::setBirthDate);
            Optional.ofNullable(patchPatientDto.getIsActive()).ifPresent(existingPatient::setIsActive);
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

    @Override
    public List<PatientShort> retrievePatients(Set<PatientEntity> patients) {
        return patients.stream().map(PatientShort::create).toList();
    }

    @Override
    public List<PatientEntity> searchByName(String name) {
        return patientRepository.findByShortNameContainingIgnoreCase(name);
    }


    static PatientEntity unwrapPatient(Optional<PatientEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, PatientEntity.class);
    }
}
