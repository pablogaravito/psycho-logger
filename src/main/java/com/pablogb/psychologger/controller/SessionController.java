package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.domain.entity.Session;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/{id}")
    public ResponseEntity<Session> getSession(@PathVariable Long id) {
        return new ResponseEntity<>(sessionService.getSession(id), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Session> saveSession(@Valid @RequestBody Session session) {
        return new ResponseEntity<>(sessionService.saveSession(session), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Long id, @Valid @RequestBody Session session) {
        return new ResponseEntity<>(sessionService.updateSession(id, session), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
