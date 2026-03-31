package com.pablogb.psychologger.service.impl;


import com.pablogb.psychologger.dto.request.OrgSettingsRequestDto;
import com.pablogb.psychologger.dto.response.OrgSettingsResponseDto;
import com.pablogb.psychologger.model.entity.OrgSettings;
import com.pablogb.psychologger.model.entity.User;
import com.pablogb.psychologger.model.enums.AuditAction;
import com.pablogb.psychologger.repository.OrgSettingsRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.AuditService;
import com.pablogb.psychologger.service.OrgSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgSettingsServiceImpl implements OrgSettingsService {

    private final OrgSettingsRepository orgSettingsRepository;
    private final SecurityUtils securityUtils;
    private AuditService auditService;

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

        // track changes before applying
        List<String> changes = new ArrayList<>();

        if (request.getDefaultCurrency() != null &&
                !request.getDefaultCurrency().equals(settings.getDefaultCurrency())) {
            changes.add("currency: " + settings.getDefaultCurrency()
                    + " → " + request.getDefaultCurrency());
        }
        if (request.getTranscriptionLanguage() != null &&
                !request.getTranscriptionLanguage().equals(settings.getTranscriptionLanguage())) {
            changes.add("transcription language: " + settings.getTranscriptionLanguage()
                    + " → " + request.getTranscriptionLanguage());
        }
        if (request.getUiLanguage() != null &&
                !request.getUiLanguage().equals(settings.getUiLanguage())) {
            changes.add("ui language: " + settings.getUiLanguage()
                    + " → " + request.getUiLanguage());
        }
        if (request.getDateFormat() != null &&
                !request.getDateFormat().equals(settings.getDateFormat())) {
            changes.add("date format: " + settings.getDateFormat()
                    + " → " + request.getDateFormat());
        }

        // apply changes
        if (request.getDefaultCurrency() != null)
            settings.setDefaultCurrency(request.getDefaultCurrency());
        if (request.getTranscriptionLanguage() != null)
            settings.setTranscriptionLanguage(request.getTranscriptionLanguage());
        if (request.getUiLanguage() != null)
            settings.setUiLanguage(request.getUiLanguage());
        if (request.getDateFormat() != null)
            settings.setDateFormat(request.getDateFormat());

        settings.setUpdatedBy(currentUser);
        OrgSettings saved = orgSettingsRepository.save(settings);

        // log
        String details = changes.isEmpty()
                ? "No changes detected"
                : String.join(", ", changes);
        auditService.log(AuditAction.ORG_SETTINGS_UPDATE, "OrgSettings",
                saved.getId(), details);

        return toResponseDto(saved);
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