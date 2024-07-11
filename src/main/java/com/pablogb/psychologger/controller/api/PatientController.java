package com.pablogb.psychologger.controller.api;

import com.pablogb.psychologger.dto.api.PatchPatientDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final SessionService sessionService;

    private final Mapper<PatientEntity, PatientDto> patientMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        PatientEntity foundPatient = patientService.getPatient(id);
        PatientDto patientDto = patientMapper.mapTo(foundPatient);
        return new ResponseEntity<>(patientDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
        PatientEntity patientEntity = patientMapper.mapFrom(patientDto);
        PatientEntity savedPatient = patientService.savePatient(patientEntity);
        return new ResponseEntity<>(patientMapper.mapTo(savedPatient), HttpStatus.CREATED);
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
        PatientEntity savedPatient = patientService.savePatient(patientEntity);
        return new ResponseEntity<>(patientMapper.mapTo(savedPatient), HttpStatus.OK);
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

    @GetMapping("/all")
    public ResponseEntity<Set<PatientDto>> getPatients() {
        Set<PatientDto> patientDtoSet = patientService.getPatients().stream().map(patientMapper::mapTo).collect(Collectors.toSet());
        return new ResponseEntity<>(patientDtoSet, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Set<PatientDto>> getActivePatients() {
        Set<PatientDto> patientDtoSet = patientService.getActivePatients().stream().map(patientMapper::mapTo).collect(Collectors.toSet());
        return new ResponseEntity<>(patientDtoSet, HttpStatus.OK);
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<SessionEntity>> getPatientSessions(@PathVariable Long id) {
        return new ResponseEntity<>(sessionService.getPatientSessions(id), HttpStatus.OK);
    }
}
