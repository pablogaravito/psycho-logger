package com.pablogb.psychologger.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSettingsResponseDto {
    private Integer id;
    private String defaultCurrency;
    private String updatedByName;
    private String transcriptionLanguage;
    private String uiLanguage;
    private String dateFormat;
}