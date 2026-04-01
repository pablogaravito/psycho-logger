package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.PatientRequestDto;
import com.pablogb.psychologger.dto.response.BirthdayPatientDto;
import com.pablogb.psychologger.dto.response.PageResponseDto;
import com.pablogb.psychologger.dto.response.PatientResponseDto;
import java.util.List;

public interface PatientService {
    //List<PatientResponseDto> getAllPatients();
    PageResponseDto<PatientResponseDto> getAllPatients(int page, int size, boolean showInactive);
    PatientResponseDto getPatientById(Integer id);
    PatientResponseDto createPatient(PatientRequestDto request);
    PatientResponseDto updatePatient(Integer id, PatientRequestDto request);
    void deactivatePatient(Integer id);
    List<PatientResponseDto> searchPatientsOrgWide(String name);
    PatientResponseDto flagPatient(Integer id, String note);
    PatientResponseDto clearFlag(Integer id);
    PatientResponseDto assignPatient(Integer patientId, Integer therapistId);
    void unassignPatient(Integer patientId);
    List<BirthdayPatientDto> getUpcomingBirthdays(boolean includeInactive);
}
