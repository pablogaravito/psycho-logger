package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.request.UserRequestDto;
import com.pablogb.psychologger.dto.response.UserResponseDto;
import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllTherapists();
    UserResponseDto getTherapistById(Integer id);
    UserResponseDto createTherapist(UserRequestDto request);
    UserResponseDto updateTherapist(Integer id, UserRequestDto request);
    void deactivateTherapist(Integer id);
}
