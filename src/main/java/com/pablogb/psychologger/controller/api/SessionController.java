package com.pablogb.psychologger.controller.api;

import com.pablogb.psychologger.dto.api.CreateSessionDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        return new ResponseEntity<>(sessionService.getSession(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SessionDto> saveSession(@Valid @RequestBody CreateSessionDto createSessionDto) {
        return new ResponseEntity<>(sessionService.saveSession(createSessionDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionDto> updateSession(@PathVariable Long id, @Valid @RequestBody CreateSessionDto createSessionDto) {
        return new ResponseEntity<>(sessionService.updateSession(id, createSessionDto), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SessionDto> partialUpdateSession(
            @PathVariable Long id,
            @RequestBody SessionDto sessionDto) {
        return new ResponseEntity<>(sessionService.partialUpdateSession(id, sessionDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> getSessions() {
        return new ResponseEntity<>(sessionService.getSessions(), HttpStatus.OK);
    }

    @GetMapping("/pages")
    public ResponseEntity<List<SessionDto>> getSessionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SessionDto> patients = sessionService.getSessionsPaginated(page, size);
        List<SessionDto> content = patients.getContent();
        return new ResponseEntity<>(content, HttpStatus.OK);
    }
}
