package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.api.PatchPatientDto;
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
        return patientRepository.findAll(PageRequest.of(page, size));
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
