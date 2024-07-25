package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.CreatePatientDto;
import com.pablogb.psychologger.dto.view.PatientView;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientCreateViewMapper implements Mapper<CreatePatientDto, PatientView> {
    private final ModelMapper modelMapper;

    @Override
    public PatientView mapTo(CreatePatientDto createPatientDto) {
        return modelMapper.map(createPatientDto, PatientView.class);
    }

    @Override
    public CreatePatientDto mapFrom(PatientView patientView) {
        return modelMapper.map(patientView, CreatePatientDto.class);
    }
}
