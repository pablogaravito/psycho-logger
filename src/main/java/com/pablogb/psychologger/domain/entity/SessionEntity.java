package com.pablogb.psychologger.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "session")
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "sessionDate", nullable = false)
    private LocalDate sessionDate;

    @NotBlank(message = "Subject cannot be blank")
    @NonNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotBlank(message = "Session's content cannot be blank")
    @NonNull
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @NonNull
    @Column(name = "is_important", nullable = false)
    private Boolean isImportant = false;

    @NonNull
    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;

    @Column(name = "next_week")
    private String nextWeek;

    @NonNull
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_session",
            joinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id")
    )
    private Set<PatientEntity> patients;
}
