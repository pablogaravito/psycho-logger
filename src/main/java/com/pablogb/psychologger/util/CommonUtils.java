package com.pablogb.psychologger.util;

import java.util.List;
import java.util.stream.Stream;

public class CommonUtils {

    private CommonUtils() {

    }
    public static List<Long> getPatientIds(String csvInput) {
        return Stream.of(csvInput.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
    }
}
