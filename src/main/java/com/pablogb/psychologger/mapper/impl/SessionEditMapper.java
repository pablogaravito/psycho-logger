package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.controller.view.dto.SessionCreateView;
import com.pablogb.psychologger.controller.view.dto.SessionEditView;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionEditMapper implements Mapper<SessionEntity, SessionEditView> {
    private final ModelMapper modelMapper;


    @Override
    public SessionEditView mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, SessionEditView.class);
    }

    @Override
    public SessionEntity mapFrom(SessionEditView sessionEditView) {
        return modelMapper.map(sessionEditView, SessionEntity.class);
    }
}
