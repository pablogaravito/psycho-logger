package com.pablogb.psychologger.domain.entity;

public enum Sex {

    MALE("M"),
    FEMALE("F");
    private String code;

    private Sex(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
