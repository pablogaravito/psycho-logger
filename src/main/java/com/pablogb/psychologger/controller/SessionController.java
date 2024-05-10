package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/{id}")
    public ResponseEntity<SessionEntity> getSession(@PathVariable Long id) {
        return new ResponseEntity<>(sessionService.getSession(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SessionEntity> saveSession(@Valid @RequestBody SessionEntity sessionEntity) {
        return new ResponseEntity<>(sessionService.saveSession(sessionEntity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionEntity> updateSession(@PathVariable Long id, @Valid @RequestBody SessionEntity sessionEntity) {
        return new ResponseEntity<>(sessionService.updateSession(id, sessionEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
