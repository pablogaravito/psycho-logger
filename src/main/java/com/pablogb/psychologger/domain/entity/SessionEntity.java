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
@Table(name = "`SESSION`")
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "SESSION_DATE", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "THEMES", nullable = false)
    private String themes;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "IS_IMPORTANT", nullable = false)
    @Builder.Default
    private Boolean isImportant = false;

    @Column(name = "IS_PAID", nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    @Column(name = "NEXT_WEEK")
    private String nextWeek;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(
            name = "PATIENT_SESSION",
            joinColumns = @JoinColumn(name = "SESSION_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PATIENT_ID", referencedColumnName = "ID")
    )
    private Set<PatientEntity> patients;
}
