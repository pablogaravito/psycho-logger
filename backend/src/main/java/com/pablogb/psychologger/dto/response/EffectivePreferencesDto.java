package com.pablogb.psychologger.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EffectivePreferencesDto {
    private String transcriptionLanguage;
    private String uiLanguage;
    private String dateFormat;
    private String timeFormat;
}
