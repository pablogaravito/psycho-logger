package com.pablogb.psychologger.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @NonNull
    @Column(name = "first_names", nullable = false)
    private String firstNames;

    @NotBlank(message = "Last Name cannot be blank")
    @NonNull
    @Column(name = "last_names", nullable = false)
    private String lastNames;

    @NotBlank(message = "Short name cannot be blank")
    @NonNull
    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Past(message = "The birth date must be in the past")
    @NonNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NonNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @NonNull
    @Column(name = "sex", nullable = false)
    private Sex sex;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_session",
            joinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id")
    )
    private Set<Session> sessions;
}
