package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.controller.view.PatientView;
import com.pablogb.psychologger.controller.view.SessionView;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionViewMapper implements Mapper<SessionEntity, SessionView> {
    private final ModelMapper modelMapper;

    @Override
    public SessionView mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, SessionView.class);
    }

    @Override
    public SessionEntity mapFrom(SessionView sessionView) {
        return modelMapper.map(sessionView, SessionEntity.class);
    }
}
