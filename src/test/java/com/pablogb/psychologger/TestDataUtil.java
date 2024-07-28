package com.pablogb.psychologger;

import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionCreationDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.model.enums.Sex;

import java.time.LocalDate;
import java.util.List;

public final class TestDataUtil {
    private TestDataUtil() {

    }

    public static PatientCreationDto createTestPatientA() {
        return PatientCreationDto.builder()
                .firstNames("Pablo")
                .lastNames("Garavito Badaracco")
                .shortName("Pablo Garavito")
                .birthDate(LocalDate.parse("1987-05-12"))
                .sex('M')
                .isActive(true)
                .build();
    }

    public static PatientCreationDto createTestPatientB() {
        return PatientCreationDto.builder()
                .firstNames("Juana")
                .lastNames("Pérez Cortéz")
                .shortName("Juanita Cortéz")
                .birthDate(LocalDate.parse("1996-08-11"))
                .sex('F')
                .isActive(true)
                .build();
    }

    public static PatientDto createIncompletePatientDto() {
        return PatientDto.builder()
                .shortName("Pando America")
                .sex(Sex.MALE)
                .isActive(true)
                .build();
    }


    public static SessionCreationDto createTestSessionA(List<Long> patientIds) {
        return SessionCreationDto.builder()
                .sessionDate(LocalDate.parse("2019-05-12"))
                .themes("trabajo niño interior")
                .content("el paciente habló de muchas cosas muy interesantes, ha sufrido mucho en la vida. Le recordé que la vida es un carnaval y que mañana el sol volverá a brillar!!! Así es, amigos, así que ya está en camino a la sanación definitiva de su alma, Dios mediante, claro está")
                .isImportant(true)
                .isPaid(true)
                .patients(patientIds)
                .build();
    }

    public static SessionCreationDto createTestSessionB(List<Long> patientIds) {
        return SessionCreationDto.builder()
                .sessionDate(LocalDate.parse("2018-08-11"))
                .themes("trabajo emocional")
                .content("la paciente habló de muchas cosas muito interesantes... su situación es complicada, ya que hay mucho desorden en su familia de origen, le recordé que aunque las cosas parezcan muy oscuras, siempre habrá una luz al final del túnel!!! y esa luz pueden ser las personas que nos aman, así es!")
                .isImportant(true)
                .isPaid(false)
                .nextWeek("trabajo relación con el padre")
                .patients(patientIds)
                .build();
    }

    public static SessionDto createIncompleteSessionDto() {
        return SessionDto.builder()
                .content("el paciente habló de puras cosas random, ez sesión amigos!!")
                .nextWeek("ya veremos papu")
                .isImportant(false)
                .build();
    }
}
