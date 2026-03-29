package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.response.EffectivePreferencesDto;
import com.pablogb.psychologger.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final UserPreferencesService userPreferencesService;

    @GetMapping
    public ResponseEntity<EffectivePreferencesDto> getEffectivePreferences() {
        return ResponseEntity.ok(EffectivePreferencesDto.builder()
                .transcriptionLanguage(userPreferencesService.getEffectiveTranscriptionLanguage())
                .uiLanguage(userPreferencesService.getEffectiveUiLanguage())
                .dateFormat(userPreferencesService.getEffectiveDateFormat())
                .timeFormat(userPreferencesService.getEffectiveTimeFormat())
                .build());
    }
}
