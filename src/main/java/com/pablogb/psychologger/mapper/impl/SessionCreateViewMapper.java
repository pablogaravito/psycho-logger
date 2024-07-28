package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.view.SessionCreateView;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionCreateViewMapper implements Mapper<SessionDto, SessionCreateView> {
    private final ModelMapper modelMapper;

    @Override
    public SessionCreateView mapTo(SessionDto sessionDto) {
        return modelMapper.map(sessionDto, SessionCreateView.class);
    }

    @Override
    public SessionDto mapFrom(SessionCreateView sessionCreateView) {
        return modelMapper.map(sessionCreateView, SessionDto.class);
    }
}
