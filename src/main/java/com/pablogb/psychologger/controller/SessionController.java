package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.domain.dao.PatchSessionDto;
import com.pablogb.psychologger.domain.dao.SessionDto;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    private final Mapper<SessionEntity, SessionDto> sessionMapper;

    @GetMapping("/greeting")
    public String testGreeting() {
        return "hello there";
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        SessionEntity foundSession = sessionService.getSession(id);
        SessionDto sessionDto = sessionMapper.mapTo(foundSession);
        return new ResponseEntity<>(sessionDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SessionDto> saveSession(@Valid @RequestBody SessionDto sessionDto) {
        SessionEntity sessionEntity = sessionMapper.mapFrom(sessionDto);
        SessionEntity savedSession = sessionService.saveSession(sessionEntity);
        return new ResponseEntity<>(sessionMapper.mapTo(savedSession), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionDto> updateSession(@PathVariable Long id, @Valid @RequestBody SessionDto sessionDto) {
        if (!sessionService.sessionExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        sessionDto.setId(id);
        SessionEntity sessionEntity = sessionMapper.mapFrom(sessionDto);
        SessionEntity savedSession = sessionService.saveSession(sessionEntity);
        return new ResponseEntity<>(sessionMapper.mapTo(savedSession), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SessionDto> partialUpdateSession(
            @PathVariable Long id,
            @RequestBody PatchSessionDto patchSessionDto) {

        if (!sessionService.sessionExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        patchSessionDto.setId(id);

        SessionEntity updatedSession = sessionService.partialUpdateSession(patchSessionDto);
        return new ResponseEntity<>(sessionMapper.mapTo(updatedSession), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Set<SessionDto>> getSessions() {
        Set<SessionDto> sessionDtoSet = sessionService.getSessions()
                .stream().map(sessionMapper::mapTo).collect(Collectors.toSet());
        return new ResponseEntity<>(sessionDtoSet, HttpStatus.OK);
    }
}
