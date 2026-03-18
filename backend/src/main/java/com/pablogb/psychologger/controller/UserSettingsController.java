package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.request.UserSettingsRequestDto;
import com.pablogb.psychologger.dto.response.SessionDefaultsDto;
import com.pablogb.psychologger.dto.response.UserSettingsResponseDto;
import com.pablogb.psychologger.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings/user")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping
    public ResponseEntity<UserSettingsResponseDto> getUserSettings() {
        return ResponseEntity.ok(userSettingsService.getUserSettings());
    }

    @PutMapping
    public ResponseEntity<UserSettingsResponseDto> updateUserSettings(
            @RequestBody UserSettingsRequestDto request) {
        return ResponseEntity.ok(userSettingsService.updateUserSettings(request));
    }

    @GetMapping("/session-defaults/{patientId}")
    public ResponseEntity<SessionDefaultsDto> getSessionDefaults(
            @PathVariable Integer patientId) {
        return ResponseEntity.ok(userSettingsService.getSessionDefaults(patientId));
    }
}
