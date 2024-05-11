package com.pablogb.psychologger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pablogb.psychologger.service.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class SessionControllerIntegrationTests {

    @Autowired
    private SessionService sessionService;

    private final ObjectMapper objectMapper;


    public SessionControllerIntegrationTests() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void testThatCreatedSessionReturnsHttpStatus201() throws Exception {

    }
}
