package com.pablogb.psychologger.service;

import com.pablogb.psychologger.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl {
    private final SessionRepository sessionRepository;


}
