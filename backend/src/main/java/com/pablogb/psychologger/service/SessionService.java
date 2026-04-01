package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.SessionRequestDto;
import com.pablogb.psychologger.dto.response.PageResponseDto;
import com.pablogb.psychologger.dto.response.SessionResponseDto;
import java.util.List;

public interface SessionService {
    SessionResponseDto getSessionById(Integer id);
    SessionResponseDto createSession(SessionRequestDto request);
    SessionResponseDto updateSession(Integer id, SessionRequestDto request);
    void deleteSession(Integer id);
    PageResponseDto<SessionResponseDto> getAllSessions(int page, int size);
    PageResponseDto<SessionResponseDto> getSessionsByPatient(Integer patientId, int page, int size);
}