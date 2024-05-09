package com.pablogb.psychologger;

import com.pablogb.psychologger.domain.dao.PatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.Sex;

import java.time.LocalDate;

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
                .sex(Sex.M)
                .isActive(true)
                .build();
    }

    public static PatientDto createTestPatientDtoA() {
        return PatientDto.builder()
                .id(1L)
                .firstNames("Pablo")
                .lastNames("Garavito Badaracco")
                .birthDate(LocalDate.parse("1987-05-12"))
                .sex(Sex.M)
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
                .sex(Sex.F)
                .isActive(true)
                .build();
    }

}
