package com.pablogb.psychologger.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pablogb.psychologger.TestDataUtil;
import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionCreationDto;
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

import java.util.List;

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
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionA(List.of(savedPatient.getId()));
        String sessionJson = objectMapper.writeValueAsString(testSessionA);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sessionJson)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testThatCreateSessionSuccessfullyReturnsSavedSession() throws Exception {
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        PatientDto savedPatient = patientService.savePatient(testPatientB);
        SessionCreationDto testSessionB = TestDataUtil.createTestSessionB(List.of(savedPatient.getId()));
        String sessionJson = objectMapper.writeValueAsString(testSessionB);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(sessionJson)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.themes").value("trabajo emocional"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nextWeek").value("trabajo relación con el padre")
                );
    }

    @Test
    void testThatCreateIncompleteSessionReturnsHttpStatus400() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatientA = patientService.savePatient(testPatientA);
        SessionDto incompleteSession = TestDataUtil.createIncompleteSessionDto();

        String jsonSession = objectMapper.writeValueAsString(incompleteSession);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSession)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testThatGetSessionsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions")
        ).andExpect(MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    void testThatGetSessionsReturnsSetOfSessions() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        PatientDto savedPatientA = patientService.savePatient(testPatientA);
        PatientDto savedPatientB = patientService.savePatient(testPatientB);

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionA(List.of(savedPatientA.getId()));
        SessionCreationDto testSessionB = TestDataUtil.createTestSessionB(List.of(savedPatientB.getId()));
        sessionService.saveSession(testSessionA);
        sessionService.saveSession(testSessionB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.jsonPath("$[*].nextWeek", Matchers.containsInAnyOrder("trabajo relación con el padre", null))
        ).andExpect(MockMvcResultMatchers.jsonPath("$[*].themes", Matchers.containsInAnyOrder("trabajo emocional", "trabajo niño interior"))
        );
    }

    @Test
    void testThatGetSessionReturnsHttpStatus200WhenSessionExists() throws Exception {
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        PatientDto savedPatient = patientService.savePatient(testPatientB);
        SessionCreationDto testSessionB = TestDataUtil.createTestSessionB(List.of(savedPatient.getId()));
        SessionDto savedSession = sessionService.saveSession(testSessionB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + savedSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    void testThatGetSessionReturnsSessionWhenSessionExists() throws Exception {
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        PatientDto savedPatient = patientService.savePatient(testPatientB);

        SessionCreationDto testSessionB = TestDataUtil.createTestSessionB(List.of(savedPatient.getId()));
        SessionDto savedSession = sessionService.saveSession(testSessionB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + savedSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.content").value(savedSession.getContent())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.themes").value("trabajo emocional")
        );
    }

    @Test
    void testThatGetSessionReturnsHttpStatus404WhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    void testThatFullUpdateSessionReturnsHttpStatus404WhenSessionDoesNotExist() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionA(List.of(savedPatient.getId()));
        String sessionJson = objectMapper.writeValueAsString(testSessionA);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThatFullUpdateSessionReturnsHttpStatus400WhenPayloadIsIncomplete() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);
        Long patientId = savedPatient.getId();
        SessionDto incompleteSession = TestDataUtil.createIncompleteSessionDto();

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionA(List.of(patientId));
        SessionDto savedSession = sessionService.saveSession(testSessionA);
        String incompleteSessionJson = objectMapper.writeValueAsString(incompleteSession);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/patients/" + savedSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompleteSessionJson)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    void testThatFullUpdateSessionReturnsHttpStatus200WhenSessionExists() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionB(List.of(savedPatient.getId()));
        SessionDto savedSession = sessionService.saveSession(testSessionA);
        SessionCreationDto testSessionB = TestDataUtil.createTestSessionB(List.of(savedPatient.getId()));

        String sessionJson = objectMapper.writeValueAsString(testSessionB);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + savedSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionJson)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThatFullUpdateSessionUpdatesExistingSession() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatientA = patientService.savePatient(testPatientA);

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionA(List.of(savedPatientA.getId()));
        SessionDto savedSession = sessionService.saveSession(testSessionA);
        SessionCreationDto testSessionB = TestDataUtil.createTestSessionA(List.of(savedPatientA.getId()));
        String updatedSessionJson = objectMapper.writeValueAsString(testSessionB);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + savedSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedSessionJson)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.themes").value(testSessionB.getThemes())
        ).andExpect(MockMvcResultMatchers.jsonPath("$.content").value(testSessionB.getContent())
        );
    }

    @Test
    void testThatPartialUpdateSessionReturnsHttpStatus404WhenSessionDoesNotExist() throws Exception {
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        patientService.savePatient(testPatientB);
        SessionDto sessionDto = TestDataUtil.createIncompleteSessionDto();

        String patientJson = objectMapper.writeValueAsString(sessionDto);
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/sessions/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThatPartialUpdateSessionReturnsUpdatedSessionAndHttpStatus200() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionA(List.of(savedPatient.getId()));
        SessionDto savedSession = sessionService.saveSession(testSessionA);

        SessionDto testSessionDto = TestDataUtil.createIncompleteSessionDto();
        String sessionJson = objectMapper.writeValueAsString(testSessionDto);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/sessions/" + savedSession.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(sessionJson)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.content").value(testSessionDto.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nextWeek").value("ya veremos papu"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThatDeleteSessionReturnsHttpStatus204WhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/sessions/99")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    void testThatDeleteSessionReturnsHttpStatus204WhenSessionExists() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionA(List.of(savedPatient.getId()));
        SessionDto savedSession = sessionService.saveSession(testSessionA);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/patients/" + savedSession.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()
        );
    }
}
