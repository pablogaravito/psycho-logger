package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.view.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.exception.EntityNotFoundException;
import com.pablogb.psychologger.mapper.impl.PatientEntityToPatientCreationDtoMapper;
import com.pablogb.psychologger.mapper.impl.PatientEntityToPatientDtoMapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.enums.Sex;
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
    private final PatientEntityToPatientDtoMapper patientEntityToPatientDtoMapper;
    private final PatientEntityToPatientCreationDtoMapper patientEntityToPatientCreationDtoMapper;

    @Override
    public PatientDto getPatient(Long id) {
        PatientEntity patientEntity = unwrapPatient(patientRepository.findById(id), id);
        return patientEntityToPatientDtoMapper.mapTo(patientEntity);
    }

    @Override
    public List<PatientDto> getPatients() {
        List<PatientEntity> patients = new ArrayList<>();
        Iterable<PatientEntity> patientEntities = patientRepository.findAll();
        patientEntities.forEach(patients::add);
        return patients.stream().map(patientEntityToPatientDtoMapper::mapTo).toList();
    }

    @Override
    public Page<PatientDto> getPatientsPaginated(int page, int size) {
        Page<PatientEntity> patientsPage = patientRepository.findByIsActiveTrue(PageRequest.of(page, size));
        return patientsPage.map(PatientDto::create);
    }

    @Override
    public List<PatientDto> getActivePatients() {
        List<PatientEntity> patientEntities = patientRepository.findByIsActiveTrue();
        return patientEntities.stream().map(patientEntityToPatientDtoMapper::mapTo).toList();
    }

    @Override
    public PatientDto savePatient(PatientCreationDto patientCreationDto) {
        PatientEntity patientEntity = patientEntityToPatientCreationDtoMapper.mapFrom(patientCreationDto);
        PatientEntity savedPatientEntity = patientRepository.save(patientEntity);
        return patientEntityToPatientDtoMapper.mapTo(savedPatientEntity);
    }

    @Override
    public PatientDto updatePatient(Long id, PatientCreationDto patientCreationDto) {
        return patientRepository.findById(id)
                .map(existingPatient -> {
                    updatePatientFields(existingPatient, patientCreationDto);
                    PatientEntity updatedPatient = patientRepository.save(existingPatient);
                    return patientEntityToPatientDtoMapper.mapTo(updatedPatient);
                }).orElseThrow(() -> new EntityNotFoundException(id, PatientEntity.class));
    }

    @Override
    public PatientDto partialUpdatePatient(Long id, PatientDto patientDto) {
        return patientRepository.findById(id)
                .map(existingPatient -> {
                    updatePatientFields(existingPatient, patientDto);
                    PatientEntity updatedPatient = patientRepository.save(existingPatient);
                    return patientEntityToPatientDtoMapper.mapTo(updatedPatient);
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
        return patientEntities.stream().map(patientEntityToPatientDtoMapper::mapTo).toList();
    }

    @Override
    public List<PatientWithBirthdayContextDto> getPatientsWithIncomingBirthdays() {
        return patientRepository.findPersonsWithUpcomingAndRecentBirthdays();
    }

    private void updatePatientFields(PatientEntity existingPatient, PatientCreationDto patientCreationDto) {
        existingPatient.setFirstNames(patientCreationDto.getFirstNames());
        existingPatient.setLastNames(patientCreationDto.getLastNames());
        existingPatient.setShortName(patientCreationDto.getShortName());
        existingPatient.setSex(Sex.getSexFromCode(patientCreationDto.getSex()));
        existingPatient.setBirthDate(patientCreationDto.getBirthDate());
        existingPatient.setIsActive(patientCreationDto.getIsActive());
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
