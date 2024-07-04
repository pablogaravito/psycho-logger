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
@Table(name = "session")
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "themes", nullable = false)
    private String themes;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_important", nullable = false)
    @Builder.Default
    private Boolean isImportant = false;

    @Column(name = "is_paid", nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    @Column(name = "next_week")
    private String nextWeek;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(
            name = "patient_session",
            joinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id")
    )
    private List<PatientEntity> patients;
}
