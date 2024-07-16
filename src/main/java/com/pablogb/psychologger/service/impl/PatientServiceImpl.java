package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        List<PatientEntity> patients = new ArrayList<>();
        Iterable<PatientEntity> patientEntities = patientRepository.findAll();
        patientEntities.forEach(patients::add);
        return patients;
    }

    @Override
    public Page<PatientEntity> getPatientsPaginated(int page, int size) {
        return patientRepository.findByIsActiveTrue(PageRequest.of(page, size));
    }


    @Override
    public List<PatientEntity> getActivePatients() {
        return patientRepository.findByIsActiveTrue();
    }



    @Override
    public PatientEntity savePatient(PatientEntity patientEntity) {
        return patientRepository.save(patientEntity);
    }

    @Override
    public PatientEntity partialUpdatePatient(PatientDto patientDto) {
        return patientRepository.findById(patientDto.getId()).map(existingPatient -> {
            Optional.ofNullable(patientDto.getFirstNames()).ifPresent(existingPatient::setFirstNames);
            Optional.ofNullable(patientDto.getLastNames()).ifPresent(existingPatient::setLastNames);
            Optional.ofNullable(patientDto.getShortName()).ifPresent(existingPatient::setShortName);
            Optional.ofNullable(patientDto.getSex()).ifPresent(existingPatient::setSex);
            Optional.ofNullable(patientDto.getBirthDate()).ifPresent(existingPatient::setBirthDate);
            Optional.ofNullable(patientDto.getIsActive()).ifPresent(existingPatient::setIsActive);
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
    public List<PatientShort> retrievePatients(List<PatientEntity> patients) {
        return patients.stream().map(PatientShort::create).toList();
    }

    @Override
    public List<PatientEntity> searchPatientByName(String name) {
        return patientRepository.searchPatientByName(name);
    }

    @Override
    public List<PatientWithBirthdayContextDto> getPatientsWithIncomingBirthdays() {
        return patientRepository.findPersonsWithUpcomingAndRecentBirthdays();
    }

    static PatientEntity unwrapPatient(Optional<PatientEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, PatientEntity.class);
    }

}
