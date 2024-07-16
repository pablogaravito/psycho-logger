package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientView;
import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientViewMapper implements Mapper<PatientDto, PatientView> {
    private final ModelMapper modelMapper;


    @Override
    public PatientView mapTo(PatientDto patientDto) {
        return modelMapper.map(patientDto, PatientView.class);
    }

    @Override
    public PatientDto mapFrom(PatientView patientView) {
        return modelMapper.map(patientView, PatientDto.class);
    }
}
