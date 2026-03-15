package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.SessionRequestDto;
import com.pablogb.psychologger.dto.response.SessionResponseDto;
import java.util.List;

public interface SessionService {
    List<SessionResponseDto> getAllSessions();
    List<SessionResponseDto> getSessionsByPatient(Integer patientId);
    SessionResponseDto getSessionById(Integer id);
    SessionResponseDto createSession(SessionRequestDto request);
    SessionResponseDto updateSession(Integer id, SessionRequestDto request);
    void deleteSession(Integer id);
}