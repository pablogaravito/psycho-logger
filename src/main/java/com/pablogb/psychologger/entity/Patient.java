package com.pablogb.psychologger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @NonNull
    @Column(name = "first_names")
    private String firstNames;

    @NotBlank(message = "Last Name cannot be blank")
    @NonNull
    @Column(name = "last_names")
    private String lastNames;

    @NotBlank(message = "Short name cannot be blank")
    @NonNull
    @Column(name = "short_name")
    private String shortName;

    @Past(message = "The birth date must be in the past")
    @NonNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NonNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @NonNull
    @Column(name = "sex", nullable = false)
    private Sex sex;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "patient_session",
            joinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id")
    )
    private Set<Session> sessions;
}
