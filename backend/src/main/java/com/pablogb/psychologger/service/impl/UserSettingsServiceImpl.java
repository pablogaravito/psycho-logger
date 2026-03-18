package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.request.UserSettingsRequestDto;
import com.pablogb.psychologger.dto.response.SessionDefaultsDto;
import com.pablogb.psychologger.dto.response.UserSettingsResponseDto;
import com.pablogb.psychologger.model.entity.OrgSettings;
import com.pablogb.psychologger.model.entity.Patient;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.model.entity.UserSettings;
import com.pablogb.psychologger.repository.OrgSettingsRepository;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.UserSettingsRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final OrgSettingsRepository orgSettingsRepository;
    private final PatientRepository patientRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public UserSettingsResponseDto getUserSettings() {
        UserSettings settings = getOrCreate();
        return toResponseDto(settings);
    }

    @Override
    @Transactional
    public UserSettingsResponseDto updateUserSettings(UserSettingsRequestDto request) {
        UserSettings settings = getOrCreate();

        if (request.getDefaultSessionDuration() != null)
            settings.setDefaultSessionDuration(request.getDefaultSessionDuration());
        if (request.getDefaultSessionPrice() != null)
            settings.setDefaultSessionPrice(request.getDefaultSessionPrice());

        return toResponseDto(userSettingsRepository.save(settings));
    }

    @Override
    @Transactional(readOnly = true)
    public SessionDefaultsDto getSessionDefaults(Integer patientId) {
        User currentUser = securityUtils.getCurrentUser();
        UserSettings userSettings = getOrCreate();

        // get org currency
        OrgSettings orgSettings = orgSettingsRepository
                .findByOrganizationId(currentUser.getOrganization().getId())
                .orElse(null);
        String currency = orgSettings != null ? orgSettings.getDefaultCurrency() : "USD";

        // price priority: patient override > user default > null
        BigDecimal price = patientRepository.findById(patientId)
                .map(Patient::getDefaultPrice)
                .filter(p -> p != null)
                .orElse(userSettings.getDefaultSessionPrice());

        return SessionDefaultsDto.builder()
                .defaultCurrency(currency)
                .defaultSessionDuration(userSettings.getDefaultSessionDuration())
                .defaultSessionPrice(price)
                .build();
    }

    private UserSettings getOrCreate() {
        User currentUser = securityUtils.getCurrentUser();
        return userSettingsRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> userSettingsRepository.save(
                        UserSettings.builder()
                                .user(currentUser)
                                .defaultSessionDuration(50)
                                .build()
                ));
    }

    private UserSettingsResponseDto toResponseDto(UserSettings settings) {
        return UserSettingsResponseDto.builder()
                .id(settings.getId())
                .defaultSessionDuration(settings.getDefaultSessionDuration())
                .defaultSessionPrice(settings.getDefaultSessionPrice())
                .build();
    }
}
