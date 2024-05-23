package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.domain.dto.PatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientMapper implements Mapper<PatientEntity, PatientDto> {

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
