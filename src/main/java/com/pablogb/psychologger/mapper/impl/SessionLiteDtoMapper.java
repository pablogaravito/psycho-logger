package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.SessionLiteDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.SessionEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionLiteDtoMapper implements Mapper<SessionEntity, SessionLiteDto> {

    private final ModelMapper modelMapper;

    @Override
    public SessionLiteDto mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, SessionLiteDto.class);
    }

    @Override
    public SessionEntity mapFrom(SessionLiteDto sessionLiteDto) {
        return modelMapper.map(sessionLiteDto, SessionEntity.class);
    }
}
