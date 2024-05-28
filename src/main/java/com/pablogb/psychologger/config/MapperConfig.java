package com.pablogb.psychologger.config;

import com.pablogb.psychologger.domain.entity.Sex;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Provider<LocalDate> localDateProvider = new AbstractProvider<>() {
            @Override
            public LocalDate get() {
                return LocalDate.now();
            }
        };

        Provider<String> stringDateProvider = new AbstractProvider<>() {
            @Override
            public String get() {
                return LocalDate.now().toString();
            }
        };

        Provider<Sex> sexStringProvider = new AbstractProvider<>() {
            @Override
            protected Sex get() {
                return Sex.FEMALE;
            }
        };

        Provider<String> defaultStringProvider = new AbstractProvider<>() {
            @Override
            protected String get() {
                return "";
            }
        };

        Converter<String, String> fromStringToString = new AbstractConverter<>() {
            @Override
            protected String convert(String s) {
                return Optional.ofNullable(s).orElse("");
            }
        };

        Converter<Sex, String> fromSexToString = new AbstractConverter<>() {
            @Override
            protected String convert(Sex sex) {
                return Optional.ofNullable(sex).orElse(Sex.FEMALE).name();
            }
        };

        Converter<String, Sex> fromStringToSex = new AbstractConverter<>() {
            @Override
            protected Sex convert(String s) {
                return Sex.valueOf(Optional.ofNullable(s).orElse("FEMALE"));
            }
        };

        Converter<String, LocalDate> toStringDate = new AbstractConverter<>() {
            @Override
            protected LocalDate convert(String source) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return Optional.ofNullable(source).isPresent() ? LocalDate.parse(source, format) : LocalDate.now();
            }
        };

        Converter<LocalDate, String> toDateString = new AbstractConverter<>() {
            @Override
            protected String convert(LocalDate source) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                return Optional.ofNullable(source).isPresent() ? source.format(format) : LocalDate.now().format(format);
            }
        };

        modelMapper.createTypeMap(String.class, LocalDate.class);
        modelMapper.addConverter(toStringDate);
        modelMapper.getTypeMap(String.class, LocalDate.class).setProvider(localDateProvider);

        modelMapper.createTypeMap(LocalDate.class, String.class);
        modelMapper.addConverter(toDateString);
        modelMapper.getTypeMap(LocalDate.class, String.class).setProvider(stringDateProvider);

        modelMapper.createTypeMap(String.class, String.class);
        modelMapper.addConverter(fromStringToString);
        modelMapper.getTypeMap(String.class, String.class).setProvider(defaultStringProvider);

        modelMapper.createTypeMap(Sex.class, String.class);
        modelMapper.addConverter(fromSexToString);
        modelMapper.getTypeMap(Sex.class, String.class).setProvider(defaultStringProvider);

        modelMapper.createTypeMap(String.class, Sex.class);
        modelMapper.addConverter(fromStringToSex);
        modelMapper.getTypeMap(String.class, Sex.class).setProvider(sexStringProvider);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
    }
}
