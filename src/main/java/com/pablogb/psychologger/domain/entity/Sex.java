package com.pablogb.psychologger.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sex {

    MALE("M"),
    FEMALE("F");
    private final String code;

}
