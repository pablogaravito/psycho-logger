package com.pablogb.psychologger.entity;

public enum Sex {
    MALE("M"),
    FEMALE("F");
    private String code;

    Sex(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
