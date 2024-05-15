package com.pablogb.psychologger;

import com.pablogb.psychologger.domain.dao.PatchPatientDto;
import com.pablogb.psychologger.domain.dao.PatchSessionDto;
import com.pablogb.psychologger.domain.dao.PatientDto;
import com.pablogb.psychologger.domain.dao.SessionDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.domain.entity.Sex;
import net.bytebuddy.asm.Advice;

import java.time.LocalDate;
import java.util.Set;

public final class TestDataUtil {
    private TestDataUtil() {

    }

    public static PatientEntity createTestPatientA() {
        return PatientEntity.builder()
                .id(1L)
                .firstNames("Pablo")
                .lastNames("Garavito Badaracco")
                .shortName("Pablo Garavito")
                .birthDate(LocalDate.parse("1987-05-12"))
                .sex(Sex.MALE)
                .isActive(true)
                .build();
    }

    public static PatientDto createTestPatientDtoA() {
        return PatientDto.builder()
                .id(1L)
                .firstNames("Pablo")
                .lastNames("Garavito Badaracco")
                .shortName("Pablo Garavito")
                .birthDate(LocalDate.parse("1987-05-12"))
                .sex(Sex.MALE)
                .isActive(true)
                .build();
    }

    public static PatientEntity createTestPatientB() {
        return PatientEntity.builder()
                .id(2L)
                .firstNames("Briseth Dayana")
                .lastNames("Pérez Alcarraz")
                .shortName("Briseth Pérez")
                .birthDate(LocalDate.parse("2008-08-11"))
                .sex(Sex.FEMALE)
                .isActive(true)
                .build();
    }

    public static PatchPatientDto createIncompletePatientDto() {
        return PatchPatientDto.builder()
                .id(1L)
                .shortName("Pando America")
                .sex(Sex.MALE)
                .isActive(true)
                .build();
    }


    public static SessionEntity createTestSessionA() {
        PatientEntity patient = createTestPatientA();
        Set<PatientEntity> patients = Set.of(patient);
        return SessionEntity.builder()
                .id(1L)
                .sessionDate(LocalDate.parse("2019-05-12"))
                .subject("trabajo niño interior")
                .content("el paciente habló de muchas cosas muy interesantes, ha sufrido mucho en la vida. Le recordé que la vida es un carnaval y que mañana el sol volverá a brillar!!! Así es, amigos, así que ya está en camino a la sanación definitiva de su alma, Dios mediante, claro está")
                .isImportant(true)
                .isPaid(true)
                .patients(patients)
                .build();
    }

    public static SessionEntity createTestSessionB() {
        PatientEntity patient = createTestPatientB();
        Set<PatientEntity> patients = Set.of(patient);
        return SessionEntity.builder()
                .id(2L)
                .sessionDate(LocalDate.parse("2018-08-11"))
                .subject("trabajo emocional")
                .content("la paciente habló de muchas cosas muito interesantes... su situación es complicada, ya que hay mucho desorden en su familia de origen, le recordé que aunque las cosas parezcan muy oscuras, siempre habrá una luz al final del túnel!!! y esa luz pueden ser las personas que nos aman, así es!")
                .isImportant(true)
                .isPaid(false)
                .nextWeek("trabajo relación con el padre")
                .patients(patients)
                .build();
    }

    public static PatchSessionDto createIncompleteSessionDto() {
        return PatchSessionDto.builder()
                .id(1L)
                .content("el paciente habló de puras cosas random, ez sesión amigos!!")
                .nextWeek("ya veremos papu")
                .isImportant(false)
                .build();
    }
}
