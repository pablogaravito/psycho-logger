package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.model.entity.PatientEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientDtoMapper implements Mapper<PatientEntity, PatientDto> {

    private final ModelMapper modelMapper;

    @Override
    public PatientDto mapTo(PatientEntity patientEntity) {
        return modelMapper.map(patientEntity, PatientDto.class);
    }

    @Override
    public PatientEntity mapFrom(PatientDto patientDto) {
        return modelMapper.map(patientDto, PatientEntity.class);
    }
}
