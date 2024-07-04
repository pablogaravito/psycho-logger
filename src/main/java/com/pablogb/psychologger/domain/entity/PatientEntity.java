package com.pablogb.psychologger.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@NamedNativeQuery(
        name = "PatientEntity.findPersonsWithUpcomingAndRecentBirthdays",
        query = "SELECT p.id, p.short_name AS shortName, FORMATDATETIME(p.birth_date, 'yyyy-MM-dd') AS birthDate " +
                "FROM PATIENT p " +
                "WHERE DAYOFYEAR(p.birth_date) - DAYOFYEAR(CURDATE()) BETWEEN 0 AND 14 " +
                "OR " +
                "DAYOFYEAR( CONCAT(YEAR(CURDATE()),'-12-31') ) - ( DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) ) BETWEEN 0 AND 14 " +
                "OR " +
                "DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) BETWEEN 0 AND 7 " +
                "OR " +
                "(DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) ) - DAYOFYEAR( CONCAT(YEAR(CURDATE()), '-12-31')) BETWEEN 0 AND 7 " +
                "ORDER BY " +
                "CASE " +
                "WHEN DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) BETWEEN 0 AND 7 THEN 1 " +
                "WHEN (DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) ) - DAYOFYEAR( CONCAT(YEAR(CURDATE()), '-12-31')) BETWEEN 0 AND 7 THEN 1 " +
                "WHEN DAYOFYEAR(p.birth_date) - DAYOFYEAR(CURDATE()) BETWEEN 0 AND 14 THEN 2 " +
                "WHEN DAYOFYEAR( CONCAT(YEAR(CURDATE()),'-12-31') ) - ( DAYOFYEAR(CURDATE()) - DAYOFYEAR(p.birth_date) ) BETWEEN 0 AND 14 THEN 2 " +
                "END, " +
                "MONTH(p.birth_date), DAY(p.birth_date)",
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

@Table(name = "patient")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_names", nullable = false)
    private String firstNames;

    @Column(name = "last_names", nullable = false)
    private String lastNames;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "sex", nullable = false)
    private Sex sex;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_session",
            joinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id")
    )
    private List<SessionEntity> sessions;
}
