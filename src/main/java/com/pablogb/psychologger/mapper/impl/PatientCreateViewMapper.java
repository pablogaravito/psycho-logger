package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.dto.view.PatientView;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientCreateViewMapper implements Mapper<PatientCreationDto, PatientView> {
    private final ModelMapper modelMapper;

    @Override
    public PatientView mapTo(PatientCreationDto patientCreationDto) {
        return modelMapper.map(patientCreationDto, PatientView.class);
    }

    @Override
    public PatientCreationDto mapFrom(PatientView patientView) {
        return modelMapper.map(patientView, PatientCreationDto.class);
    }
}
