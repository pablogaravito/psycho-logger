package com.pablogb.psychologger.controller.api;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.CreatePatientDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.entity.SessionEntity;
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

    private final Mapper<PatientEntity, PatientDto> patientMapper;
    private final Mapper<PatientEntity, CreatePatientDto> createPatientDtoMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        PatientDto foundPatient = patientService.getPatient(id);
        return new ResponseEntity<>(foundPatient, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody CreatePatientDto createPatientDto) {
        PatientEntity patientEntity = createPatientDtoMapper.mapFrom(createPatientDto);
        PatientDto savedPatient = patientService.savePatient(patientMapper.mapTo(patientEntity));
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> fullUpdatePatient(
            @PathVariable Long id,
            @Valid @RequestBody CreatePatientDto createPatientDto) {
        if (!patientService.patientExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        createPatientDto.setId(id);
        PatientEntity patientEntity = createPatientDtoMapper.mapFrom(createPatientDto);
        PatientDto savedPatient = patientService.savePatient(patientMapper.mapTo(patientEntity));
        return new ResponseEntity<>(savedPatient, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PatientDto> partialUpdatePatient(
            @PathVariable Long id,
            @RequestBody PatientDto patientDto) {

        if (!patientService.patientExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        patientDto.setId(id);
        PatientDto updatedPatient = patientService.partialUpdatePatient(patientDto);
        return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
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
        Page<PatientEntity> patientsPage = patientService.getPatientsPaginated(page, size);
        Page<PatientDto> patients = patientsPage.map(PatientDto::create);
        List<PatientDto> content = patients.getContent();
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<SessionEntity>> getPatientSessions(@PathVariable Long id) {
        return new ResponseEntity<>(sessionService.getPatientSessions(id), HttpStatus.OK);
    }
}
