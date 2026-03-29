package com.pablogb.psychologger.service.impl;


import com.pablogb.psychologger.dto.request.OrgSettingsRequestDto;
import com.pablogb.psychologger.dto.response.OrgSettingsResponseDto;
import com.pablogb.psychologger.model.entity.OrgSettings;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.repository.OrgSettingsRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.OrgSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrgSettingsServiceImpl implements OrgSettingsService {

    private final OrgSettingsRepository orgSettingsRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public OrgSettingsResponseDto getOrgSettings() {
        OrgSettings settings = getOrCreate();
        return toResponseDto(settings);
    }

    @Override
    @Transactional
    public OrgSettingsResponseDto updateOrgSettings(OrgSettingsRequestDto request) {
        User currentUser = securityUtils.getCurrentUser();
        OrgSettings settings = getOrCreate();

        if (request.getDefaultCurrency() != null)
            settings.setDefaultCurrency(request.getDefaultCurrency());
        if (request.getTranscriptionLanguage() != null)
            settings.setTranscriptionLanguage(request.getTranscriptionLanguage());
        if (request.getUiLanguage() != null)
            settings.setUiLanguage(request.getUiLanguage());
        if (request.getDateFormat() != null)
            settings.setDateFormat(request.getDateFormat());

        settings.setUpdatedBy(currentUser);
        return toResponseDto(orgSettingsRepository.save(settings));
    }

    private OrgSettings getOrCreate() {
        Integer orgId = securityUtils.getCurrentOrgId();
        return orgSettingsRepository.findByOrganizationId(orgId)
                .orElseGet(() -> orgSettingsRepository.save(
                        OrgSettings.builder()
                                .organization(securityUtils.getCurrentUser().getOrganization())
                                .defaultCurrency("SOL")
                                .transcriptionLanguage("es")
                                .uiLanguage("es")
                                .dateFormat("DD/MM/YYYY")
                                .build()
                ));
    }

    private OrgSettingsResponseDto toResponseDto(OrgSettings settings) {
        String updatedByName = settings.getUpdatedBy() != null
                ? settings.getUpdatedBy().getFirstName() + " " + settings.getUpdatedBy().getLastName()
                : null;
        return OrgSettingsResponseDto.builder()
                .id(settings.getId())
                .defaultCurrency(settings.getDefaultCurrency())
                .transcriptionLanguage(settings.getTranscriptionLanguage())
                .uiLanguage(settings.getUiLanguage())
                .dateFormat(settings.getDateFormat())
                .updatedByName(updatedByName)
                .build();
    }
}