package com.pablogb.psychologger.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {
    private DateUtils() {
    }

    public static String formatVerboseDate(LocalDate input) {
        Locale spanishLocale = new Locale("es", "PE");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy",spanishLocale);
        return input.format(format);
    }

    public static String formatShortDate(LocalDate input) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return input.format(format);
    }
}
