package com.pablogb.psychologger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "session")
public class Session {

    public Session(@NonNull String subject, @NonNull String content) {
        this.subject = subject;
        this.content = content;
        this.isPaid = false;
        this.isImportant = false;
    }

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
    private boolean isImportant;



    @NonNull
    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "patient_session",
            joinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id")
    )
    private Set<Patient> patients;




}
