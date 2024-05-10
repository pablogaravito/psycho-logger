package com.pablogb.psychologger.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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

    @NotBlank(message = "Subject cannot be blank")
    @NonNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotBlank(message = "Session's content cannot be blank")
    @NonNull
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
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sessions")
    private Set<PatientEntity> patients;
}
