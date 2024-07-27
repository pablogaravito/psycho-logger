package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientEntityToPatientCreationDtoMapper implements Mapper<PatientEntity, PatientCreationDto> {

    private final ModelMapper modelMapper;

    @Override
    public PatientCreationDto mapTo(PatientEntity patientEntity) {
        return modelMapper.map(patientEntity, PatientCreationDto.class);
    }

    @Override
    public PatientEntity mapFrom(PatientCreationDto patientCreationDto) {
        return modelMapper.map(patientCreationDto, PatientEntity.class);
    }
}
