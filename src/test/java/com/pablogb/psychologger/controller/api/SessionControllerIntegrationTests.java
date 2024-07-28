package com.pablogb.psychologger.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pablogb.psychologger.TestDataUtil;
import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SessionControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private PatientService patientService;

    private final ObjectMapper objectMapper;


    public SessionControllerIntegrationTests() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void testThatCreatedSessionReturnsHttpStatus201() throws Exception {
//        SessionDto testSessionA = TestDataUtil.createTestSessionA();
//        String sessionJson = objectMapper.writeValueAsString(testSessionA);
//        mockMvc.perform(MockMvcRequestBuilders.post("/sessions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(sessionJson)
//        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testThatCreateSessionSuccessfullyReturnsSavedSession() throws Exception {
//        SessionDto testSessionB = TestDataUtil.createTestSessionB();
//        String sessionJson = objectMapper.writeValueAsString(testSessionB);
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post("/sessions")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(sessionJson)
//                ).andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.subject").value("trabajo emocional"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.nextWeek").value("trabajo relación con el padre")
//                );
    }

    @Test
    void testThatCreateIncompleteSessionReturnsHttpStatus400() throws Exception {
        SessionDto incompleteSession = TestDataUtil.createIncompleteSessionDto();

        String jsonSession = objectMapper.writeValueAsString(incompleteSession);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSession)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testThatGetSessionsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/sessions")
        ).andExpect(MockMvcResultMatchers.status().isOk()
        );
    }

//    @Test
//    void testThatGetSessionsReturnsSetOfSessions() throws Exception {
//        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
////        testPatientA.setId(null);
//        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
////        testPatientB.setId(null);
//        SessionEntity testSessionA = TestDataUtil.createTestSessionA();
//        SessionEntity testSessionB = TestDataUtil.createTestSessionB();
//        testSessionA.setPatients(List.of(testPatientA));
//        testSessionB.setPatients(List.of(testPatientB));
//
//        sessionService.saveSession(testSessionA);
//        sessionService.saveSession(testSessionB);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.get("/sessions")
//                        .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(MockMvcResultMatchers.jsonPath("$[*].nextWeek", Matchers.containsInAnyOrder("trabajo relación con el padre", null))
//        ).andExpect(MockMvcResultMatchers.jsonPath("$[*].subject", Matchers.containsInAnyOrder("trabajo emocional", "trabajo niño interior"))
//        );
//    }
//
//    @Test
//    void testThatGetSessionReturnsHttpStatus200WhenSessionExists() throws Exception {
//        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
////        testPatientB.setId(null);
//        SessionEntity testSessionB = TestDataUtil.createTestSessionB();
//        testSessionB.setPatients(List.of(testPatientB));
//
//        SessionEntity savedSession = sessionService.saveSession(testSessionB);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.get("/sessions/" + savedSession.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(
//                MockMvcResultMatchers.status().isOk()
//        );
//    }
//
//    @Test
//    void testThatGetSessionReturnsSessionWhenSessionExists() throws Exception {
//        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
////        testPatientB.setId(null);
//        SessionEntity testSession = TestDataUtil.createTestSessionB();
//        testSession.setPatients(List.of(testPatientB));
//        SessionEntity savedSession = sessionService.saveSession(testSession);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.get("/sessions/" + savedSession.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(
//                MockMvcResultMatchers.jsonPath("$.content").value(savedSession.getContent())
//        ).andExpect(
//                MockMvcResultMatchers.jsonPath("$.subject").value("trabajo emocional")
//        );
//    }


//    @Test
//    void testThatGetSessionReturnsHttpStatus404WhenSessionDoesNotExist() throws Exception {
//        mockMvc.perform(
//                MockMvcRequestBuilders.get("/sessions/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(
//                MockMvcResultMatchers.status().isNotFound()
//        );
//    }
//
//    @Test
//    void testThatFullUpdateSessionReturnsHttpStatus404WhenSessionDoesNotExist() throws Exception {
//        SessionEntity testSessionA = TestDataUtil.createTestSessionA();
//        String sessionJson = objectMapper.writeValueAsString(testSessionA);
//        mockMvc.perform(
//                MockMvcRequestBuilders.put("/sessions/99")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(sessionJson)
//        ).andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    void testThatFullUpdateSessionReturnsHttpStatus400WhenPayloadIsIncomplete() throws Exception {
//
//        SessionDto incompleteSession = TestDataUtil.createIncompleteSessionDto();
//        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
//        testPatientA.setId(null);
//        SessionEntity testSessionA = TestDataUtil.createTestSessionA();
//        testSessionA.setPatients(List.of(testPatientA));
//        SessionEntity savedSession = sessionService.saveSession(testSessionA);
//        String incompleteSessionJson = objectMapper.writeValueAsString(incompleteSession);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.put("/patients/" + savedSession.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(incompleteSessionJson)
//        ).andExpect(MockMvcResultMatchers.status().isBadRequest()
//        );
//    }
//
//    @Test
//    void testThatFullUpdateSessionReturnsHttpStatus200WhenSessionExists() throws Exception {
//        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
//        testPatientA.setId(null);
//        SessionEntity testSessionA = TestDataUtil.createTestSessionA();
//        SessionEntity testSessionB = TestDataUtil.createTestSessionB();
//        testSessionA.setPatients(List.of(testPatientA));
//        testSessionB.setPatients(List.of(testPatientA));
//
//        SessionEntity savedSession = sessionService.saveSession(testSessionA);
//        String sessionJson = objectMapper.writeValueAsString(testSessionB);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.put("/sessions/" + savedSession.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(sessionJson)
//        ).andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    void testThatFullUpdateSessionUpdatesExistingSession() throws Exception {
//        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
//        testPatientA.setId(null);
//        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
//        testPatientB.setId(null);
//        SessionEntity testSessionA = TestDataUtil.createTestSessionA();
//        SessionEntity updatedSession = TestDataUtil.createTestSessionB();
//        testSessionA.setPatients(List.of(testPatientA));
//        updatedSession.setPatients(List.of(testPatientB));
//
//        SessionEntity savedSession = sessionService.saveSession(testSessionA);
//        String updatedSessionJson = objectMapper.writeValueAsString(updatedSession);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.put("/sessions/" + savedSession.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(updatedSessionJson)
//        ).andExpect(MockMvcResultMatchers.jsonPath("$.subject").value(updatedSession.getThemes())
//        ).andExpect(MockMvcResultMatchers.jsonPath("$.content").value(updatedSession.getContent())
//        );
//    }
//
//    @Test
//    void testThatPartialUpdateSessionReturnsHttpStatus404WhenSessionDoesNotExist() throws Exception {
//        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
//        testPatientB.setId(null);
//        SessionEntity testSessionB = TestDataUtil.createTestSessionB();
//        testSessionB.setPatients(List.of(testPatientB));
//
//        String patientJson = objectMapper.writeValueAsString(testSessionB);
//        mockMvc.perform(
//                MockMvcRequestBuilders.patch("/sessions/99")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(patientJson)
//        ).andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    void testThatPartialUpdateSessionReturnsUpdatedSessionAndHttpStatus200() throws Exception {
//        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
//        testPatientA.setId(null);
//
//        SessionEntity testSessionA = TestDataUtil.createTestSessionA();
//        testSessionA.setPatients(List.of(testPatientA));
//        SessionEntity savedSession = sessionService.saveSession(testSessionA);
//
//        SessionDto testSessionDto = TestDataUtil.createIncompleteSessionDto();
//        String sessionJson = objectMapper.writeValueAsString(testSessionDto);
//        mockMvc.perform(
//                        MockMvcRequestBuilders.patch("/sessions/" + savedSession.getId())
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(sessionJson)
//                ).andExpect(MockMvcResultMatchers.jsonPath("$.content").value(testSessionDto.getContent()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.nextWeek").value("ya veremos papu"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    void testThatDeleteSessionReturnsHttpStatus204WhenSessionDoesNotExist() throws Exception {
//        mockMvc.perform(
//                MockMvcRequestBuilders.delete("/sessions/99")
//                        .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(MockMvcResultMatchers.status().isNoContent()
//        );
//    }
//
//    @Test
//    void testThatDeleteSessionReturnsHttpStatus204WhenSessionExists() throws Exception {
//        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
//        testPatientA.setId(null);
//
//        SessionEntity testSessionA = TestDataUtil.createTestSessionA();
//        testSessionA.setPatients(List.of(testPatientA));
//        SessionEntity savedSession = sessionService.saveSession(testSessionA);
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.delete("/patients/" + savedSession.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(MockMvcResultMatchers.status().isNoContent()
//        );
//    }
}
