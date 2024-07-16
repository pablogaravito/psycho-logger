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

    private final Mapper<PatientEntity, CreatePatientDto> patientMapper;

    @GetMapping("/{id}")
    public ResponseEntity<CreatePatientDto> getPatient(@PathVariable Long id) {
        PatientEntity foundPatient = patientService.getPatient(id);
        CreatePatientDto createPatientDto = patientMapper.mapTo(foundPatient);
        return new ResponseEntity<>(createPatientDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CreatePatientDto> createPatient(@Valid @RequestBody CreatePatientDto createPatientDto) {
        PatientEntity patientEntity = patientMapper.mapFrom(createPatientDto);
        PatientEntity savedPatient = patientService.savePatient(patientEntity);
        return new ResponseEntity<>(patientMapper.mapTo(savedPatient), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreatePatientDto> fullUpdatePatient(
            @PathVariable Long id,
            @Valid @RequestBody CreatePatientDto createPatientDto) {
        if (!patientService.patientExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        createPatientDto.setId(id);
        PatientEntity patientEntity = patientMapper.mapFrom(createPatientDto);
        PatientEntity savedPatient = patientService.savePatient(patientEntity);
        return new ResponseEntity<>(patientMapper.mapTo(savedPatient), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CreatePatientDto> partialUpdatePatient(
            @PathVariable Long id,
            @RequestBody PatientDto patientDto) {

        if (!patientService.patientExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        patientDto.setId(id);

        PatientEntity updatedPatient = patientService.partialUpdatePatient(patientDto);
        return new ResponseEntity<>(patientMapper.mapTo(updatedPatient), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PatientDto>> getPatients() {
        //List<PatientDto> patientDtoList = patientService.getPatients().stream().map(patientMapper::mapTo).toList();
        List<PatientDto> patientDtoList = patientService.getPatients().stream().map(PatientDto::create).toList();
        return new ResponseEntity<>(patientDtoList, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PatientDto>> getActivePatients() {
//        List<CreatePatientDto> createPatientDtoList = patientService.getActivePatients().stream().map(patientMapper::mapTo).toList();
        List<PatientDto> patientDtoList = patientService.getActivePatients().stream().map(PatientDto::create).toList();
        return new ResponseEntity<>(patientDtoList, HttpStatus.OK);
    }

    @GetMapping("/pages")
    public ResponseEntity<Page<PatientDto>> getActivePatientsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PatientEntity> patientsPage = patientService.getPatientsPaginated(page, size);
        Page<PatientDto> patients = patientsPage.map(PatientDto::create);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<SessionEntity>> getPatientSessions(@PathVariable Long id) {
        return new ResponseEntity<>(sessionService.getPatientSessions(id), HttpStatus.OK);
    }
}
