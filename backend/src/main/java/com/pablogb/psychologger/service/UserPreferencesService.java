package com.pablogb.psychologger.service;

public interface UserPreferencesService {
    String getEffectiveTranscriptionLanguage();
    String getEffectiveUiLanguage();
    String getEffectiveDateFormat();
    String getEffectiveTimeFormat();
}