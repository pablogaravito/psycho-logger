package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientDtoToPatienShortMapper implements Mapper<PatientDto, PatientShort> {
    private final ModelMapper modelMapper;

    @Override
    public PatientShort mapTo(PatientDto patientDto) {
        return modelMapper.map(patientDto, PatientShort.class);
    }

    @Override
    public PatientDto mapFrom(PatientShort patientShort) {
        return modelMapper.map(patientShort, PatientDto.class);
    }
}
