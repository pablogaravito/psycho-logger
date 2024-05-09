package com.pablogb.psychologger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pablogb.psychologger.TestDataUtil;
import com.pablogb.psychologger.domain.dao.PatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

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

    private ObjectMapper objectMapper;


    public PatientControllerIntegrationTests() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void publicEndPoint() throws Exception {
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstNames").value("Briseth Dayana"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortName").value("Briseth Pérez"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("2008-08-11")
                );
    }

    @Test
    void testThatListPatientsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients")
        ).andExpect(MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    void testThatListPatientsReturnsListOfPatients() throws Exception {
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        patientService.savePatient(testPatientA);
        patientService.savePatient(testPatientB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.jsonPath("$[0].firstNames").value("Pablo")
        ).andExpect(MockMvcResultMatchers.jsonPath("$[1].firstNames").value("Briseth Dayana")
        ).andExpect(MockMvcResultMatchers.jsonPath("$[1].birthDate").value("2008-08-11")
        );
    }

    @Test
    void testThatGetPatientReturnsHttpStatus200WhenPatientExists() throws Exception {
        PatientEntity testPatientB = TestDataUtil.createTestPatientB();
        patientService.savePatient(testPatientB);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/patients/1")
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
                MockMvcResultMatchers.jsonPath("$.firstNames").value("Briseth Dayana")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.birthDate").value("2008-08-11")
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
    void testThatFullUpdateReturnsHttpStatus200WhenPatientExists() throws Exception {
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
        updatedPatient.setId(savedPatient.getId());
        String patientJson = objectMapper.writeValueAsString(updatedPatient);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson)
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
        PatientEntity testPatientA = TestDataUtil.createTestPatientA();
        PatientEntity savedPatient = patientService.savePatient(testPatientA);
        PatientDto testPatientDtoA = TestDataUtil.createTestPatientDtoA();
        testPatientDtoA.setFirstNames("Pablis");
        String patientJson = objectMapper.writeValueAsString(testPatientDtoA);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/patients/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patientJson)
                ).andExpect(MockMvcResultMatchers.jsonPath("$.firstNames").value("Pablis"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastNames").value("Garavito Badaracco"))
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
}
