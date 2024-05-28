package com.pablogb.psychologger.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
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
    private Set<SessionEntity> sessions;
}
