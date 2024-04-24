package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.entity.Patient;
import com.pablogb.psychologger.entity.Session;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/{id")
    public ResponseEntity<Session> getSession(@PathVariable Long id) {
        return new ResponseEntity<>(sessionService.getSession(id), HttpStatus.OK);
    }

    @PostMapping("/session}")
    public ResponseEntity<Session> saveSession(@Valid @RequestBody Session session) {
        return new ResponseEntity<>(sessionService.saveSession(session), HttpStatus.CREATED);
    }

    @PutMapping("/session/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Long id, @Valid @RequestBody Session session) {
        return new ResponseEntity<>(sessionService.updateSession(id, session), HttpStatus.OK);
    }

    @DeleteMapping("/session/{id}")
    public ResponseEntity<HttpStatus> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
