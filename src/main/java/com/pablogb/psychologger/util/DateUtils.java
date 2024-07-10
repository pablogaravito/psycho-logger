package com.pablogb.psychologger.util;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateUtils {
    private DateUtils() {
    }

    public static String formatLongDate(LocalDate input) {
        Locale spanishLocale = new Locale("es", "PE");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy",spanishLocale);
        return input.format(format);
    }

    public static String formatIntermediateDate(LocalDate input) {
        Locale spanishLocale = new Locale("es", "PE");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy",spanishLocale);
        return input.format(format);
    }

    public static String formatShortDate(LocalDate input) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return input.format(format);
    }

    public static String formatBirthdayDate(String input) {
        Locale spanishLocale = new Locale("es", "PE");
        LocalDate birthdayLocalDate = LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate birthdayCurrentYear = birthdayLocalDate.withYear(Year.now().getValue());
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", spanishLocale);
        return birthdayCurrentYear.format(format);
    }
}
