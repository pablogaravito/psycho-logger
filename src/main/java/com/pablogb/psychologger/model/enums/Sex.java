package com.pablogb.psychologger.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Sex {

    MALE('M'),
    FEMALE('F');
    private final Character code;
    public static Sex getSexFromCode(Character code) {
        return Stream.of(Sex.values()).filter(s -> Objects.equals(code, s.code))
                .findFirst().orElse(Sex.FEMALE);
    }
}
