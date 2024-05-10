package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.domain.dao.PatchPatientDto;
import com.pablogb.psychologger.domain.dao.PatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    private final Mapper<PatientEntity, PatientDto> patientMapper;

    @GetMapping("/greeting")
    public String testGreeting() {
        return "hello there";
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        PatientEntity foundPatient = patientService.getPatient(id);
        PatientDto patientDto = patientMapper.mapTo(foundPatient);
        return new ResponseEntity<>(patientDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
        PatientEntity patientEntity = patientMapper.mapFrom(patientDto);
        PatientEntity savedPatientEntity = patientService.savePatient(patientEntity);
        return new ResponseEntity<>(patientMapper.mapTo(savedPatientEntity) , HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> fullUpdatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDto patientDto) {
        if (!patientService.patientExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        patientDto.setId(id);
        PatientEntity patientEntity = patientMapper.mapFrom(patientDto);
        PatientEntity savedPatientEntity = patientService.savePatient(patientEntity);
        return new ResponseEntity<>(patientMapper.mapTo(savedPatientEntity), HttpStatus.OK) ;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PatientDto> partialUpdatePatient(
            @PathVariable Long id,
            @RequestBody PatchPatientDto patchPatientDto) {

        if (!patientService.patientExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        patchPatientDto.setId(id);

        PatientEntity updatedPatient = patientService.partialUpdatePatient(patchPatientDto);
        return new ResponseEntity<>(patientMapper.mapTo(updatedPatient), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<PatientEntity>> getPatients() {
        return new ResponseEntity<>(patientService.getPatients(), HttpStatus.OK);
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<Set<SessionEntity>> getPatientSessions(@PathVariable Long id) {
        return new ResponseEntity<>(patientService.getPatientSessions(id), HttpStatus.OK);
    }
}
