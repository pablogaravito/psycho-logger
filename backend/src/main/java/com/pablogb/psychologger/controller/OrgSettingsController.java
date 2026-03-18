package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.request.OrgSettingsRequestDto;
import com.pablogb.psychologger.dto.response.OrgSettingsResponseDto;
import com.pablogb.psychologger.service.OrgSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings/org")
@RequiredArgsConstructor
public class OrgSettingsController {

    private final OrgSettingsService orgSettingsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrgSettingsResponseDto> getOrgSettings() {
        return ResponseEntity.ok(orgSettingsService.getOrgSettings());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrgSettingsResponseDto> updateOrgSettings(
            @RequestBody OrgSettingsRequestDto request) {
        return ResponseEntity.ok(orgSettingsService.updateOrgSettings(request));
    }
}