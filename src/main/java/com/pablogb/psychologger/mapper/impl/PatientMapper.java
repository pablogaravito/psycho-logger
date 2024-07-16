package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.CreatePatientDto;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientMapper implements Mapper<PatientEntity, CreatePatientDto> {

    private final ModelMapper modelMapper;


    @Override
    public CreatePatientDto mapTo(PatientEntity patientEntity) {
        return modelMapper.map(patientEntity, CreatePatientDto.class);
    }

    @Override
    public PatientEntity mapFrom(CreatePatientDto createPatientDto) {
        return modelMapper.map(createPatientDto, PatientEntity.class);
    }
}
