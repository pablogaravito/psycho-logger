package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.api.CreatePatientDto;
import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import org.springframework.data.domain.Page;

import java.util.List;


public interface PatientService {
    PatientDto getPatient(Long id);
    List<PatientDto> getPatients();
    Page<PatientDto> getPatientsPaginated(int page, int size);
    List<PatientDto> getActivePatients();
    PatientDto savePatient(CreatePatientDto patientDto);
    PatientDto updatePatient(Long id, CreatePatientDto patientDto);
    PatientDto partialUpdatePatient(Long id, PatientDto patientDto);
    void deletePatient(Long id);
    boolean patientExists(Long id);
    List<PatientShort> retrievePatients(List<PatientEntity> patients);
    List<PatientDto> searchPatientByName(String name);
    List<PatientWithBirthdayContextDto> getPatientsWithIncomingBirthdays();
}
