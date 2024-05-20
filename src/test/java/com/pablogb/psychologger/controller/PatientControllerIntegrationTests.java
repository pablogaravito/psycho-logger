package com.pablogb.psychologger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pablogb.psychologger.TestDataUtil;
import com.pablogb.psychologger.domain.dao.PatchPatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.service.PatientService;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class PatientControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    private final ObjectMapper objectMapper;


    public PatientControllerIntegrationTests() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void greeting() throws Exception {
        mockMvc.perform(get("/patients/greeting"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("there")));
    }

    @Test
    void testThatCreatedPatientReturnsHttpStatus201() throws Exception {
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        testPatientA.setId(null);
        String patientJson = objectMapper.writeValueAsString(testPatientA);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testThatCreatePatientSuccessfullyReturnsSavedPatient() throws Exception {
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        testPatientB.setId(null);
        String patientJson = objectMapper.writeValueAsString(testPatientB);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patientJson)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstNames").value("Puerca"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortName").value("Puerca Pérez"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("1996-08-11")
                );
    }

    @Test
    void testThatCreateIncompletePatientReturnsHttpStatus400() throws Exception {
        PatchPatientDto incompletePatient = TestDataUtil.createIncompletePatientDto();

        String jsonPatient = objectMapper.writeValueAsString(incompletePatient);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatient)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testThatGetPatientsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients")
        ).andExpect(MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    void testThatGetPatientsReturnsSetOfPatients() throws Exception {
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        patientService.savePatient(testPatientA);
        patientService.savePatient(testPatientB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.jsonPath("$[*].firstNames", Matchers.containsInAnyOrder("Pablo", "Puerca"))
        ).andExpect(MockMvcResultMatchers.jsonPath("$[*].birthDate", Matchers.containsInAnyOrder("1987-05-12", "1996-08-11"))
        );
    }

    @Test
    void testThatGetPatientReturnsHttpStatus200WhenPatientExists() throws Exception {
        PatientEntity savedPatient = patientService.savePatient(TestDataUtil.createTestPatientB());
        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    void testThatGetPatientReturnsPatientWhenPatientExists() throws Exception {
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        patientService.savePatient(testPatientB);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.firstNames").value("Puerca")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.birthDate").value("1996-08-11")
        );
    }

    @Test
    void testThatGetPatientReturnsHttpStatus404WhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    void testThatFullUpdatePatientReturnsHttpStatus404WhenPatientDoesNotExist() throws Exception {
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        String patientJson = objectMapper.writeValueAsString(testPatientA);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/patients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThatFullUpdatePatientReturnsHttpStatus400WhenPayloadIsIncomplete() throws Exception {
        PatchPatientDto incompletePatient = TestDataUtil.createIncompletePatientDto();
        PatientEntity savedPatient = patientService.savePatient(TestDataUtil.createTestPatientA());
        String incompletePatientJson = objectMapper.writeValueAsString(incompletePatient);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompletePatientJson)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    void testThatFullUpdatePatientReturnsHttpStatus200WhenPatientExists() throws Exception {
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        patientService.savePatient(testPatientA);
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        String patientJson = objectMapper.writeValueAsString(testPatientB);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThatFullUpdatePatientUpdatesExistingPatient() throws Exception {
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        PatientEntity savedPatient = patientService.savePatient(testPatientB);
        PatientEntity updatedPatient = TestDataUtil.createTestPatientA();
        updatedPatient.setId(null);
        String updatedPatientJson = objectMapper.writeValueAsString(updatedPatient);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPatientJson)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.firstNames").value(updatedPatient.getFirstNames())
        ).andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value(updatedPatient.getBirthDate().toString())
        );
    }

    @Test
    void testThatPartialUpdatePatientReturnsHttpStatus404WhenPatientDoesNotExist() throws Exception {
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        String patientJson = objectMapper.writeValueAsString(testPatientB);
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/patients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThatPartialUpdatePatientReturnsUpdatedPatientAndHttpStatus200() throws Exception {
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        PatientEntity savedPatient = patientService.savePatient(testPatientB);
        PatchPatientDto testPatientDTo = TestDataUtil.createIncompletePatientDto();

        String patientJson = objectMapper.writeValueAsString(testPatientDTo);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/patients/" + savedPatient.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patientJson)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.shortName").value("Pando America"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sex").value("MALE"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testThatDeletePatientReturnsHttpStatus204WhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/patients/99")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    void testThatDeletePatientReturnsHttpStatus204WhenPatientExists() throws Exception {
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        PatientEntity savedPatient = patientService.savePatient(testPatientA);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    void testThatGetPatientSessionsReturnsHttpStatus404WhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients/99/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testThatGetPatientSessionsReturnsHttpStatus200WhenPatientExists() throws Exception {
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        PatientEntity savedPatient = patientService.savePatient(testPatientA);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients/" + savedPatient.getId() + "/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void testThatGetPatientSessionsReturnsListOfSessionsWhenSessionsExists() throws Exception {
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        testPatientA.setId(null);
        SessionEntity testSessionA = TestDataUtil.createTestSessionA();
        SessionEntity testSessionB = TestDataUtil.createTestSessionB();

        testPatientA.setSessions(Set.of(testSessionA, testSessionB));

        PatientEntity savedPatient = patientService.savePatient(testPatientA);


        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients/" + savedPatient.getId() + "/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.content().string(containsStringIgnoringCase("interior"))
        ).andExpect(MockMvcResultMatchers.content().string(containsStringIgnoringCase("carnaval"))
        );
    }

}
