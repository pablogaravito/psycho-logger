package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.entity.Patient;
import com.pablogb.psychologger.entity.Session;
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
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/greeting")
    public String testGreeting() {
        return "hello there";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        return new ResponseEntity<>(patientService.getPatient(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> savePatient(@Valid @RequestBody Patient patient) {
        patientService.saveStudent(patient);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        return new ResponseEntity<>(patientService.updatePatient(id, patient), HttpStatus.OK) ;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getPatients() {
        return new ResponseEntity<>(patientService.getPatients(), HttpStatus.OK);
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<Set<Session>> getPatientSessions(@PathVariable Long id) {
        return new ResponseEntity<>(patientService.getPatientSessions(id), HttpStatus.OK);
    }
}
