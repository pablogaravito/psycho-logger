package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.PatientRequestDto;
import com.pablogb.psychologger.dto.response.PatientResponseDto;
import java.util.List;

public interface PatientService {
    List<PatientResponseDto> getAllPatients();
    PatientResponseDto getPatientById(Integer id);
    PatientResponseDto createPatient(PatientRequestDto request);
    PatientResponseDto updatePatient(Integer id, PatientRequestDto request);
    void deactivatePatient(Integer id);
}
