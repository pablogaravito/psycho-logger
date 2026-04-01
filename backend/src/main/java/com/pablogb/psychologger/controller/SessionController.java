package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.request.SessionRequestDto;
import com.pablogb.psychologger.dto.response.PageResponseDto;
import com.pablogb.psychologger.dto.response.SessionResponseDto;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<PageResponseDto<SessionResponseDto>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(sessionService.getAllSessions(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDto> getSessionById(@PathVariable Integer id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<PageResponseDto<SessionResponseDto>> getSessionsByPatient(
            @PathVariable Integer patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sessionService.getSessionsByPatient(
                patientId, page, size));
    }

    @PostMapping
    public ResponseEntity<SessionResponseDto> createSession(@Valid @RequestBody SessionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createSession(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionResponseDto> updateSession(
            @PathVariable Integer id,
            @Valid @RequestBody SessionRequestDto request) {
        return ResponseEntity.ok(sessionService.updateSession(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Integer id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
