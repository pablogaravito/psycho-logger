package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientCreationDtoToPatientDtoMapper implements Mapper<PatientCreationDto, PatientDto> {

    private final ModelMapper modelMapper;

    @Override
    public PatientDto mapTo(PatientCreationDto patientCreationDto) {
        return modelMapper.map(patientCreationDto, PatientDto.class);
    }

    @Override
    public PatientCreationDto mapFrom(PatientDto patientDto) {
        return modelMapper.map(patientDto, PatientCreationDto.class);
    }
}
