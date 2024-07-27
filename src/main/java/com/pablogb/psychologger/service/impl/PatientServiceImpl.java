package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.api.CreatePatientDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.view.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.mapper.impl.CreatePatientDtoMapper;
import com.pablogb.psychologger.mapper.impl.PatientDtoMapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
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
    private final PatientDtoMapper patientDtoMapper;
    private final CreatePatientDtoMapper createPatientDtoMapper;

    @Override
    public PatientDto getPatient(Long id) {
        PatientEntity patientEntity = unwrapPatient(patientRepository.findById(id), id);
        return patientDtoMapper.mapTo(patientEntity);
    }

    @Override
    public List<PatientDto> getPatients() {
        List<PatientEntity> patients = new ArrayList<>();
        Iterable<PatientEntity> patientEntities = patientRepository.findAll();
        patientEntities.forEach(patients::add);
        return patients.stream().map(patientDtoMapper::mapTo).toList();
    }

    @Override
    public Page<PatientDto> getPatientsPaginated(int page, int size) {
        Page<PatientEntity> patientsPage = patientRepository.findByIsActiveTrue(PageRequest.of(page, size));
        return patientsPage.map(PatientDto::create);
    }

    @Override
    public List<PatientDto> getActivePatients() {
        List<PatientEntity> patientEntities = patientRepository.findByIsActiveTrue();
        return patientEntities.stream().map(patientDtoMapper::mapTo).toList();
    }

    @Override
    public PatientDto savePatient(CreatePatientDto createPatientDto) {
        PatientEntity patientEntity = createPatientDtoMapper.mapFrom(createPatientDto);
        PatientEntity savedPatientEntity = patientRepository.save(patientEntity);
        return patientDtoMapper.mapTo(savedPatientEntity);
    }

    @Override
    public PatientDto updatePatient(Long id, CreatePatientDto createPatientDto) {
        return patientRepository.findById(id)
                .map(existingPatient -> {
                    updatePatientFields(existingPatient, createPatientDto);
                    PatientEntity updatedPatient = patientRepository.save(existingPatient);
                    return patientDtoMapper.mapTo(updatedPatient);
                }).orElseThrow(() -> new EntityNotFoundException(id, PatientEntity.class));
    }

    @Override
    public PatientDto partialUpdatePatient(Long id, PatientDto patientDto) {
        return patientRepository.findById(id)
                .map(existingPatient -> {
                    updatePatientFields(existingPatient, patientDto);
                    PatientEntity updatedPatient = patientRepository.save(existingPatient);
                    return patientDtoMapper.mapTo(updatedPatient);
                }).orElseThrow(() -> new EntityNotFoundException(id, PatientEntity.class));
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
    public List<PatientDto> searchPatientByName(String name) {
        List<PatientEntity> patientEntities = patientRepository.searchPatientByName(name);
        return patientEntities.stream().map(patientDtoMapper::mapTo).toList();
    }

    @Override
    public List<PatientWithBirthdayContextDto> getPatientsWithIncomingBirthdays() {
        return patientRepository.findPersonsWithUpcomingAndRecentBirthdays();
    }

    private void updatePatientFields(PatientEntity existingPatient, CreatePatientDto createPatientDto) {
        existingPatient.setFirstNames(createPatientDto.getFirstNames());
        existingPatient.setLastNames(createPatientDto.getLastNames());
        existingPatient.setShortName(createPatientDto.getShortName());
        existingPatient.setSex(createPatientDto.getSex());
        existingPatient.setBirthDate(createPatientDto.getBirthDate());
        existingPatient.setIsActive(createPatientDto.getIsActive());
    }

    private void updatePatientFields(PatientEntity existingPatient, PatientDto patientDto) {
        Optional.ofNullable(patientDto.getFirstNames()).ifPresent(existingPatient::setFirstNames);
        Optional.ofNullable(patientDto.getLastNames()).ifPresent(existingPatient::setLastNames);
        Optional.ofNullable(patientDto.getShortName()).ifPresent(existingPatient::setShortName);
        Optional.ofNullable(patientDto.getSex()).ifPresent(existingPatient::setSex);
        Optional.ofNullable(patientDto.getBirthDate()).ifPresent(existingPatient::setBirthDate);
        Optional.ofNullable(patientDto.getIsActive()).ifPresent(existingPatient::setIsActive);
    }

    private

    static PatientEntity unwrapPatient(Optional<PatientEntity> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, PatientEntity.class);
    }
}
