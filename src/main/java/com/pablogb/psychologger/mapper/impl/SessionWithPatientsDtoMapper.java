package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.SessionWithPatientsDto;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionWithPatientsDtoMapper implements Mapper<SessionEntity, SessionWithPatientsDto> {

    private final ModelMapper modelMapper;
    @Override
    public SessionWithPatientsDto mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, SessionWithPatientsDto.class);
    }

    @Override
    public SessionEntity mapFrom(SessionWithPatientsDto sessionWithPatientsDto) {
        return modelMapper.map(sessionWithPatientsDto, SessionEntity.class);
    }
}
