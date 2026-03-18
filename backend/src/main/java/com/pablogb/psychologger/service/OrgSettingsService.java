package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.OrgSettingsRequestDto;
import com.pablogb.psychologger.dto.response.OrgSettingsResponseDto;

public interface OrgSettingsService {
    OrgSettingsResponseDto getOrgSettings();
    OrgSettingsResponseDto updateOrgSettings(OrgSettingsRequestDto request);
}
