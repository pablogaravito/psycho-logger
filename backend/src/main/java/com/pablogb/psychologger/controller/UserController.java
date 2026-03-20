package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.request.UserRequestDto;
import com.pablogb.psychologger.dto.response.UserResponseDto;
import com.pablogb.psychologger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllTherapists() {
        return ResponseEntity.ok(userService.getAllTherapists());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getTherapistById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getTherapistById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createTherapist(
            @RequestBody UserRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createTherapist(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateTherapist(
            @PathVariable Integer id,
            @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(userService.updateTherapist(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateTherapist(@PathVariable Integer id) {
        userService.deactivateTherapist(id);
        return ResponseEntity.noContent().build();
    }
}