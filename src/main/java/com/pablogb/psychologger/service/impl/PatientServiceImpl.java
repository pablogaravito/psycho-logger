package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.model.entity.SessionEntity;
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
    private final  Mapper<PatientEntity, PatientDto> patientDtoMapper;

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
    public Page<PatientEntity> getPatientsPaginated(int page, int size) {
        return patientRepository.findByIsActiveTrue(PageRequest.of(page, size));
    }


    @Override
    public List<PatientDto> getActivePatients() {
        List<PatientEntity> patientEntities = patientRepository.findByIsActiveTrue();
        return patientEntities.stream().map(patientDtoMapper::mapTo).toList();
    }

    @Override
    public PatientDto savePatient(PatientDto patientDto) {
        PatientEntity patientEntity = patientDtoMapper.mapFrom(patientDto);
        PatientEntity savedPatientEntity = patientRepository.save(patientEntity);
        return patientDtoMapper.mapTo(savedPatientEntity);
    }

    @Override
    public PatientDto partialUpdatePatient(PatientDto patientDto) {
        return patientRepository.findById(patientDto.getId()).map(existingPatient -> {
            Optional.ofNullable(patientDto.getFirstNames()).ifPresent(existingPatient::setFirstNames);
            Optional.ofNullable(patientDto.getLastNames()).ifPresent(existingPatient::setLastNames);
            Optional.ofNullable(patientDto.getShortName()).ifPresent(existingPatient::setShortName);
            Optional.ofNullable(patientDto.getSex()).ifPresent(existingPatient::setSex);
            Optional.ofNullable(patientDto.getBirthDate()).ifPresent(existingPatient::setBirthDate);
            Optional.ofNullable(patientDto.getIsActive()).ifPresent(existingPatient::setIsActive);
            PatientEntity savedPatient = patientRepository.save(existingPatient);
            return patientDtoMapper.mapTo(savedPatient);
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
    public List<PatientDto> searchPatientByName(String name) {
        List<PatientEntity> patientEntities = patientRepository.searchPatientByName(name);
        return patientEntities.stream().map(patientDtoMapper::mapTo).toList();
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
