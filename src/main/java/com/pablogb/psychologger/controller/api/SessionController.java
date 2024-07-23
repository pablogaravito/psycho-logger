package com.pablogb.psychologger.controller.api;

import com.pablogb.psychologger.dto.api.CreateSessionDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    private final Mapper<SessionEntity, SessionDto> sessionDtoMapper;
    private final Mapper<SessionEntity, CreateSessionDto> createSessionDtoMapper;

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        SessionDto foundSession = sessionService.getSession(id);
        return new ResponseEntity<>(foundSession, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SessionDto> saveSession(@Valid @RequestBody CreateSessionDto createSessionDto) {
        return new ResponseEntity<>(sessionService.saveSession(createSessionDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionDto> updateSession(@PathVariable Long id, @Valid @RequestBody CreateSessionDto createSessionDto) {
        if (!sessionService.sessionExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        createSessionDto.setId(id);
        SessionDto sessionDto = sessionService.updateSession(createSessionDto);
        return new ResponseEntity<>(sessionDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SessionDto> partialUpdateSession(
            @PathVariable Long id,
            @RequestBody SessionDto sessionDto) {

        if (!sessionService.sessionExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        sessionDto.setId(id);
        return new ResponseEntity<>(sessionService.partialUpdateSession(sessionDto), HttpStatus.OK);
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
}
