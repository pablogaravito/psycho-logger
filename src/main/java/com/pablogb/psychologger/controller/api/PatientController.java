package com.pablogb.psychologger.controller.api;

import com.pablogb.psychologger.dto.api.CreatePatientDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final SessionService sessionService;

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        return new ResponseEntity<>(patientService.getPatient(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PatientDto> savePatient(@Valid @RequestBody CreatePatientDto createPatientDto) {
        return new ResponseEntity<>(patientService.savePatient(createPatientDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody CreatePatientDto createPatientDto) {
        return new ResponseEntity<>(patientService.updatePatient(id, createPatientDto), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PatientDto> partialUpdatePatient(
            @PathVariable Long id,
            @RequestBody PatientDto patientDto) {
        return new ResponseEntity<>(patientService.partialUpdatePatient(id, patientDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PatientDto>> getPatients() {
        return new ResponseEntity<>(patientService.getPatients(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PatientDto>> getActivePatients() {
        return new ResponseEntity<>(patientService.getActivePatients(), HttpStatus.OK);
    }

    @GetMapping("/pages")
    public ResponseEntity<List<PatientDto>> getActivePatientsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PatientDto> patients = patientService.getPatientsPaginated(page, size);
        List<PatientDto> content = patients.getContent();
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<SessionDto>> getPatientSessions(@PathVariable Long id) {
        return new ResponseEntity<>(sessionService.getPatientSessions(id), HttpStatus.OK);
    }
}
