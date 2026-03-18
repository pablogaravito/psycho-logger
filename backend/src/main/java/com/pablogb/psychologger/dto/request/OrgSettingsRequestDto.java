package com.pablogb.psychologger.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSettingsRequestDto {
    private String defaultCurrency;
    private String preferredLanguage;
}
