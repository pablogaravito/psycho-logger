package com.pablogb.psychologger.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Sex {

    MALE("M"),
    FEMALE("F");
    private final String code;
    public static Sex getSexFromCode(String code) {
        return Stream.of(Sex.values()).filter(s -> Objects.equals(code, s.code))
                .findFirst().orElse(Sex.FEMALE);
    }
}
