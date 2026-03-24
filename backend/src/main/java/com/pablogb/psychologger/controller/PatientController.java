package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.request.PatientRequestDto;
import com.pablogb.psychologger.dto.response.BirthdayPatientDto;
import com.pablogb.psychologger.dto.response.PatientResponseDto;
import com.pablogb.psychologger.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable Integer id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@Valid @RequestBody PatientRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable Integer id,
            @Valid @RequestBody PatientRequestDto request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivatePatient(@PathVariable Integer id) {
        patientService.deactivatePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/birthdays")
    public ResponseEntity<List<BirthdayPatientDto>> getUpcomingBirthdays(
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return ResponseEntity.ok(patientService.getUpcomingBirthdays(includeInactive));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PatientResponseDto>> searchPatients(
            @RequestParam String name) {
        return ResponseEntity.ok(patientService.searchPatientsOrgWide(name));
    }

    @PutMapping("/{id}/flag")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponseDto> flagPatient(
            @PathVariable Integer id,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(patientService.flagPatient(id, note));
    }

    @PutMapping("/{id}/clear-flag")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponseDto> clearFlag(@PathVariable Integer id) {
        return ResponseEntity.ok(patientService.clearFlag(id));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientResponseDto> assignPatient(
            @PathVariable Integer id,
            @RequestParam Integer therapistId) {
        return ResponseEntity.ok(patientService.assignPatient(id, therapistId));
    }

    @PutMapping("/{id}/unassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unassignPatient(@PathVariable Integer id) {
        patientService.unassignPatient(id);
        return ResponseEntity.noContent().build();
    }
}
