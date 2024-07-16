package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import org.springframework.data.domain.Page;

import java.util.List;


public interface PatientService {
    PatientEntity getPatient(Long id);
    List<PatientEntity> getPatients();
    Page<PatientEntity> getPatientsPaginated(int page, int size);
    List<PatientEntity> getActivePatients();
    PatientEntity savePatient(PatientEntity patientEntity);
    PatientEntity partialUpdatePatient(PatientDto patientDto);
    void deletePatient(Long id);
    boolean patientExists(Long id);
    List<PatientShort> retrievePatients(List<PatientEntity> patients);
    List<PatientEntity> searchPatientByName(String name);
    List<PatientWithBirthdayContextDto> getPatientsWithIncomingBirthdays();
}
