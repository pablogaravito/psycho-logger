package com.pablogb.psychologger.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pablogb.psychologger.TestDataUtil;
import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionCreationDto;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import jakarta.transaction.Transactional;
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

import static org.hamcrest.Matchers.containsStringIgnoringCase;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private Mapper<PatientCreationDto, PatientDto> patientCreationDtoToPatientDtoMapper;

    private final ObjectMapper objectMapper;

    public PatientControllerIntegrationTests() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testThatCreatedPatientReturnsHttpStatus201() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        String patientJson = objectMapper.writeValueAsString(testPatientA);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testThatCreatePatientSuccessfullyReturnsSavedPatient() throws Exception {
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        String patientJson = objectMapper.writeValueAsString(testPatientB);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patientJson)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstNames").value("Juana"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortName").value("Juanita Cortéz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("1996-08-11")
                );
    }

    @Test
    void testThatCreateIncompletePatientReturnsHttpStatus400() throws Exception {
        PatientDto incompletePatient = TestDataUtil.createIncompletePatientDto();

        String jsonPatient = objectMapper.writeValueAsString(incompletePatient);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatient)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testThatGetPatientsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients")
        ).andExpect(MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    void testThatGetPatientsReturnsSetOfPatients() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        patientService.savePatient(testPatientA);
        patientService.savePatient(testPatientB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.jsonPath("$[*].firstNames", Matchers.containsInAnyOrder("Pablo", "Juana"))
        ).andExpect(MockMvcResultMatchers.jsonPath("$[*].birthDate", Matchers.containsInAnyOrder("1987-05-12", "1996-08-11"))
        );
    }

    @Test
    void testThatGetPatientReturnsHttpStatus200WhenPatientExists() throws Exception {
        PatientDto savedPatient = patientService.savePatient(TestDataUtil.createTestPatientB());
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    void testThatGetPatientReturnsPatientWhenPatientExists() throws Exception {
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        patientService.savePatient(testPatientB);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.firstNames").value("Juana")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.birthDate").value("1996-08-11")
        );
    }

    @Test
    void testThatGetPatientReturnsHttpStatus404WhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    void testThatFullUpdatePatientReturnsHttpStatus404WhenPatientDoesNotExist() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        String patientJson = objectMapper.writeValueAsString(testPatientA);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/patients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThatFullUpdatePatientReturnsHttpStatus400WhenPayloadIsIncomplete() throws Exception {
        PatientDto incompletePatient = TestDataUtil.createIncompletePatientDto();
        PatientDto savedPatient = patientService.savePatient(TestDataUtil.createTestPatientA());
        String incompletePatientJson = objectMapper.writeValueAsString(incompletePatient);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompletePatientJson)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    void testThatFullUpdatePatientReturnsHttpStatus200WhenPatientExists() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        patientService.savePatient(testPatientA);
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        String patientJson = objectMapper.writeValueAsString(testPatientB);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThatFullUpdatePatientUpdatesExistingPatient() throws Exception {
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        PatientDto savedPatient = patientService.savePatient(testPatientB);
        PatientCreationDto updatedPatient = TestDataUtil.createTestPatientA();
        updatedPatient.setId(null);
        String updatedPatientJson = objectMapper.writeValueAsString(updatedPatient);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPatientJson)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.firstNames").value(updatedPatient.getFirstNames())
        ).andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value(updatedPatient.getBirthDate().toString())
        );
    }

    @Test
    void testThatPartialUpdatePatientReturnsHttpStatus404WhenPatientDoesNotExist() throws Exception {
        PatientDto testPatientB = patientCreationDtoToPatientDtoMapper.mapTo(TestDataUtil.createTestPatientB());
        String patientJson = objectMapper.writeValueAsString(testPatientB);
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/patients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThatPartialUpdatePatientReturnsUpdatedPatientAndHttpStatus200() throws Exception {
        PatientCreationDto testPatientB = TestDataUtil.createTestPatientB();
        PatientDto savedPatient = patientService.savePatient(testPatientB);
        PatientDto testPatientDTo = TestDataUtil.createIncompletePatientDto();

        String patientJson = objectMapper.writeValueAsString(testPatientDTo);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/patients/" + savedPatient.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patientJson)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.shortName").value("Pando America"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sex").value("MALE"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThatDeletePatientReturnsHttpStatus204WhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/patients/99")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    void testThatDeletePatientReturnsHttpStatus204WhenPatientExists() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    void testThatGetPatientSessionsReturnsHttpStatus404WhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients/99/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThatGetPatientSessionsReturnsHttpStatus200WhenPatientExists() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients/" + savedPatient.getId() + "/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void testThatGetPatientSessionsReturnsListOfSessionsWhenSessionsExists() throws Exception {
        PatientCreationDto testPatientA = TestDataUtil.createTestPatientA();
        PatientDto savedPatient = patientService.savePatient(testPatientA);

        SessionCreationDto testSessionA = TestDataUtil.createTestSessionA(List.of(savedPatient.getId()));
        SessionCreationDto testSessionB = TestDataUtil.createTestSessionB(List.of(savedPatient.getId()));
        sessionService.saveSession(testSessionA);
        sessionService.saveSession(testSessionB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients/" + savedPatient.getId() + "/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.content().string(containsStringIgnoringCase("interior"))
        ).andExpect(MockMvcResultMatchers.content().string(containsStringIgnoringCase("carnaval"))
        ).andExpect(MockMvcResultMatchers.content().string(containsStringIgnoringCase("desorden"))
        );
    }
}
