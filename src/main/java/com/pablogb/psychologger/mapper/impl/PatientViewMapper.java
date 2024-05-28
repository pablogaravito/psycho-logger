package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.controller.view.PatientView;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientViewMapper implements Mapper<PatientEntity, PatientView> {
    private final ModelMapper modelMapper;

    @Override
    public PatientView mapTo(PatientEntity patientEntity) {
        return modelMapper.map(patientEntity, PatientView.class);
    }

    @Override
    public PatientEntity mapFrom(PatientView patientView) {
        return modelMapper.map(patientView, PatientEntity.class);
    }
}
