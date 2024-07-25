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
        PatientDto dto = modelMapper.map(patientEntity, PatientDto.class);
        dto.setCreatedAt(patientEntity.getCreatedAt());
        return dto;
    }

    @Override
    public PatientEntity mapFrom(PatientDto patientDto) {
        PatientEntity entity = modelMapper.map(patientDto, PatientEntity.class);
        entity.setCreatedAt(null);
        return entity;
    }

    public void updateEntityFromDto(PatientDto dto, PatientEntity entity) {
        modelMapper.map(dto, entity);
        entity.setCreatedAt(entity.getCreatedAt());
    }
}
