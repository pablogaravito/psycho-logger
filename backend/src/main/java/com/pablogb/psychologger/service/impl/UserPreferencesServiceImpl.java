package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.model.entity.OrgSettings;
import com.pablogb.psychologger.model.entity.UserSettings;
import com.pablogb.psychologger.repository.OrgSettingsRepository;
import com.pablogb.psychologger.repository.UserSettingsRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final UserSettingsRepository userSettingsRepository;
    private final OrgSettingsRepository orgSettingsRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public String getEffectiveTranscriptionLanguage() {
        return resolve(
                getUserSettings().getTranscriptionLanguage(),
                getOrgSettings().getTranscriptionLanguage(),
                "es"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String getEffectiveUiLanguage() {
        return resolve(
                getUserSettings().getUiLanguage(),
                getOrgSettings().getUiLanguage(),
                "es"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String getEffectiveDateFormat() {
        return resolve(
                getUserSettings().getDateFormat(),
                getOrgSettings().getDateFormat(),
                "DD/MM/YYYY"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String getEffectiveTimeFormat() {
        // no org level — just user setting with hardcoded fallback
        String userValue = getUserSettings().getTimeFormat();
        return (userValue != null && !userValue.isBlank()) ? userValue : "24h";
    }

    // user override → org default → hardcoded fallback
    private String resolve(String userValue, String orgValue, String fallback) {
        if (userValue != null && !userValue.isBlank()) return userValue;
        if (orgValue != null && !orgValue.isBlank()) return orgValue;
        return fallback;
    }

    private UserSettings getUserSettings() {
        return userSettingsRepository
                .findByUserId(securityUtils.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User settings not found"));
    }

    private OrgSettings getOrgSettings() {
        return orgSettingsRepository
                .findByOrganizationId(securityUtils.getCurrentOrgId())
                .orElseThrow(() -> new RuntimeException("Org settings not found"));
    }
}