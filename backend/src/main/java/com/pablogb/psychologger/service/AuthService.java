package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.LoginRequestDto;
import com.pablogb.psychologger.dto.request.RegisterRequestDto;
import com.pablogb.psychologger.dto.response.AuthResponseDto;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto request);
    AuthResponseDto register(RegisterRequestDto request);
}
