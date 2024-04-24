package com.pablogb.psychologger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "session")
public class Session {

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
    private boolean isImportant = false;



    @NonNull
    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "patient_session",
            joinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id")
    )
    private Set<Patient> patients;




}
