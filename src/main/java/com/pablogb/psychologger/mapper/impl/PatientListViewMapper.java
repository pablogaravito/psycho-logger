package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.controller.view.PatientListView;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PatientListViewMapper implements Mapper<PatientEntity, PatientListView> {

    private final ModelMapper modelMapper;

    @Override
    public PatientListView mapTo(PatientEntity patientEntity) {
        return modelMapper.map(patientEntity, PatientListView.class);
    }

    @Override
    public PatientEntity mapFrom(PatientListView patientListView) {
        return modelMapper.map(patientListView, PatientEntity.class);
    }
}
