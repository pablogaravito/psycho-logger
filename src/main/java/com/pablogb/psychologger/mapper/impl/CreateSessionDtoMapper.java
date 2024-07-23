package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.CreateSessionDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.SessionEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSessionDtoMapper implements Mapper<SessionEntity, CreateSessionDto> {

    private final ModelMapper modelMapper;

    @Override
    public CreateSessionDto mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, CreateSessionDto.class);
    }

    @Override
    public SessionEntity mapFrom(CreateSessionDto createSessionDto) {
        return modelMapper.map(createSessionDto, SessionEntity.class);
    }
}
