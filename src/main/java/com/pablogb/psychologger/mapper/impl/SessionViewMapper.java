package com.pablogb.psychologger.mapper.impl;

import com.pablogb.psychologger.dto.view.SessionCreateView;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionViewMapper implements Mapper<SessionEntity, SessionCreateView> {
    private final ModelMapper modelMapper;

    @Override
    public SessionCreateView mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, SessionCreateView.class);
    }

    @Override
    public SessionEntity mapFrom(SessionCreateView sessionCreateView) {
        return modelMapper.map(sessionCreateView, SessionEntity.class);
    }
}
