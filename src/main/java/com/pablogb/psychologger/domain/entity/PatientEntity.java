package com.pablogb.psychologger.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@NamedNativeQuery(
        name = "PatientEntity.findPersonsWithUpcomingAndRecentBirthdays",
        query = "SELECT p.ID, p.SHORT_NAME AS shortName, FORMATDATETIME(p.BIRTH_DATE, 'yyyy-MM-dd') AS birthDate " +
                "FROM PATIENT p " +
                "WHERE DAYOFYEAR(p.BIRTH_DATE) - DAYOFYEAR(CURDATE()) BETWEEN 0 AND 14 " +
                "OR " +
                "DAYOFYEAR( CONCAT(YEAR(CURDATE()),'-12-31') ) - ( DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.BIRTH_DATE) ) BETWEEN 0 AND 14 " +
                "OR " +
                "DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.BIRTH_DATE) BETWEEN 0 AND 7 " +
                "OR " +
                "(DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.BIRTH_DATE) ) - DAYOFYEAR( CONCAT(YEAR(CURDATE()), '-12-31')) BETWEEN 0 AND 7 " +
                "ORDER BY " +
                "CASE " +
                "WHEN DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.BIRTH_DATE) BETWEEN 0 AND 7 THEN 1 " +
                "WHEN (DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.BIRTH_DATE) ) - DAYOFYEAR( CONCAT(YEAR(CURDATE()), '-12-31')) BETWEEN 0 AND 7 THEN 1 " +
                "WHEN DAYOFYEAR(p.BIRTH_DATE) - DAYOFYEAR(CURDATE()) BETWEEN 0 AND 14 THEN 2 " +
                "WHEN DAYOFYEAR( CONCAT(YEAR(CURDATE()),'-12-31') ) - ( DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.BIRTH_DATE) ) BETWEEN 0 AND 14 THEN 2 " +
                "END, " +
                "MONTH(p.BIRTH_DATE), DAY(p.BIRTH_DATE)",
        resultSetMapping = "Mapping.PatientWithBirthdayContextDto"
)
@SqlResultSetMapping(
        name = "Mapping.PatientWithBirthdayContextDto",
        classes = @ConstructorResult(
                targetClass = com.pablogb.psychologger.domain.dto.PatientWithBirthdayContextDto.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "shortName", type = String.class),
                        @ColumnResult(name = "birthDate", type = String.class)
                }
        )
)

@Table(name = "PATIENT")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIRST_NAMES", nullable = false)
    private String firstNames;

    @Column(name = "LAST_NAMES", nullable = false)
    private String lastNames;

    @Column(name = "SHORT_NAME", nullable = false)
    private String shortName;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "BIRTH_DATE", nullable = false)
    private LocalDate birthDate;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "SEX", nullable = false)
    private Sex sex;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "PATIENT_SESSION",
            joinColumns = @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "SESSION_ID", referencedColumnName = "ID")
    )
    private Set<SessionEntity> sessions;
}
