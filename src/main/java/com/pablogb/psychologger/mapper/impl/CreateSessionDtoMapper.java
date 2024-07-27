package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.SessionCreationDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.SessionEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSessionDtoMapper implements Mapper<SessionEntity, SessionCreationDto> {

    private final ModelMapper modelMapper;

    @Override
    public SessionCreationDto mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, SessionCreationDto.class);
    }

    @Override
    public SessionEntity mapFrom(SessionCreationDto sessionCreationDto) {
        return modelMapper.map(sessionCreationDto, SessionEntity.class);
    }
}
