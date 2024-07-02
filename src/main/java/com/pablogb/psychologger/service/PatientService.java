package com.pablogb.psychologger.service;

import com.pablogb.psychologger.controller.gui.view.PatientShort;
import com.pablogb.psychologger.domain.dto.PatchPatientDto;
import com.pablogb.psychologger.domain.dto.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;


public interface PatientService {
    PatientEntity getPatient(Long id);
    Set<PatientEntity> getPatients();
    Page<PatientEntity> getPatientsPaginated(int page, int size);
    Set<PatientEntity> getActivePatients();
    Set<SessionEntity> getPatientSessions(Long id);
    PatientEntity savePatient(PatientEntity patientEntity);
    PatientEntity partialUpdatePatient(PatchPatientDto patchPatientDto);
    void deletePatient(Long id);
    boolean patientExists(Long id);
    List<PatientShort> retrievePatients(Set<PatientEntity> patients);
    List<PatientEntity> searchPatientByName(String name);
    List<PatientWithBirthdayContextDto> getPatientsWithIncomingBirthdays();
}
