package com.pablogb.psychologger.domain.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

//@Converter(autoApply = true)
public class SexConverter {
        //implements AttributeConverter<Sex, String> {
//    @Override
//    public String convertToDatabaseColumn(Sex sex) {
//        if (sex == null) {
//            return null;
//        }
//        return sex.getCode();
//    }
//
//    @Override
//    public Sex convertToEntityAttribute(String code) {
//        if (code == null) {
//            return null;
//        }
//
//        return Stream.of(Sex.values())
//                .filter(c -> c.getCode().equals(code))
//                .findFirst()
//                .orElseThrow(IllegalArgumentException::new);
//    }
}
