package com.pablogb.psychologger.config;

import com.pablogb.psychologger.model.entity.PatientEntity;
import com.pablogb.psychologger.model.enums.Sex;
import com.pablogb.psychologger.util.DateUtils;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Configuration
public class MapperConfig {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setSkipNullEnabled(true)
                .setPropertyCondition(Conditions.isNotNull());

        modelMapper.addConverter(new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(MappingContext<String, LocalDate> context) {
                String source = context.getSource();
                return Optional.ofNullable(source)
                        .map(s -> LocalDate.parse(s, DATE_FORMATTER))
                        .orElseGet(LocalDate::now);
            }
        });

        modelMapper.addConverter(new Converter<LocalDate, String>() {
            @Override
            public String convert(MappingContext<LocalDate, String> context) {
                LocalDate source = context.getSource();
                return Optional.ofNullable(source)
                        .map(DateUtils::formatShortDate)
                        .orElseGet(() -> DateUtils.formatShortDate(LocalDate.now()));
            }
        });

        modelMapper.addConverter(new Converter<String, String>() {
            @Override
            public String convert(MappingContext<String, String> context) {
                return Optional.ofNullable(context.getSource()).orElse("");
            }
        });

        modelMapper.addConverter(new Converter<Sex, Character>() {
            @Override
            public Character convert(MappingContext<Sex, Character> context) {
                Sex source = context.getSource();
                return Optional.ofNullable(source).map(Sex::getCode).orElse('F');
            }
        });

        modelMapper.addConverter(new Converter<Character, Sex>() {
            @Override
            public Sex convert(MappingContext<Character, Sex> context) {
                Character source = context.getSource();
                return Sex.getSexFromCode(source);
            }
        });

        modelMapper.addConverter(new Converter<PatientEntity, String>() {
            @Override
            public String convert(MappingContext<PatientEntity, String> context) {
                PatientEntity source = context.getSource();
                return Optional.ofNullable(source)
                        .map(PatientEntity::getId)
                        .map(Object::toString)
                        .orElse("");
            }
        });
        return modelMapper;
    }
}
