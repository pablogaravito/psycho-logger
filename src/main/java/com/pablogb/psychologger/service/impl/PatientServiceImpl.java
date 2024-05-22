package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.domain.dao.PatchPatientDto;
import com.pablogb.psychologger.domain.dao.PatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public Set<PatientEntity> getPatients() {
        return new HashSet<>((Collection) patientRepository.findAll());
    }

    @Override
    public Set<SessionEntity> getPatientSessions(Long id) {
//        PatientEntity patientEntity = getPatient(id);
//        System.out.println(patientEntity);
//        System.out.println(patientEntity.getSessions());
//        return patientEntity.getSessions();
        return patientRepository.getSessionsFromPatient(id);
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

    static PatientEntity unwrapPatient(Optional<PatientEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, PatientEntity.class);
    }
}
