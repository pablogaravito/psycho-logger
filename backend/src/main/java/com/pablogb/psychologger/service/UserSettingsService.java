package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.UserSettingsRequestDto;
import com.pablogb.psychologger.dto.response.SessionDefaultsDto;
import com.pablogb.psychologger.dto.response.UserSettingsResponseDto;

public interface UserSettingsService {
    UserSettingsResponseDto getUserSettings();
    UserSettingsResponseDto updateUserSettings(UserSettingsRequestDto request);
    SessionDefaultsDto getSessionDefaults(Integer patientId);
}
