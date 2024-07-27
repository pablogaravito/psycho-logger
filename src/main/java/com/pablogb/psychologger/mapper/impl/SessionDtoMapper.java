package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionDtoMapper implements Mapper<SessionEntity, SessionDto> {

    private final ModelMapper modelMapper;
    @Override
    public SessionDto mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, SessionDto.class);
    }

    @Override
    public SessionEntity mapFrom(SessionDto sessionDto) {
        return modelMapper.map(sessionDto, SessionEntity.class);
    }
}
