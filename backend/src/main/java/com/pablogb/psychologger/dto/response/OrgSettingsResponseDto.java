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
    private String preferredLanguage;
    private String updatedByName;
}