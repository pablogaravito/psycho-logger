package com.pablogb.psychologger.controller.view;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionView {

    private Long id;

    @NonNull
    private String sessionDate;

    @NotBlank(message = "Subject cannot be blank")
    @NonNull
    private String subject;

    @NotBlank(message = "Session's content cannot be blank")
    @NonNull
    private String content;

    @NonNull
    @Builder.Default
    private Boolean isImportant = false;

    @NonNull
    @Builder.Default
    private Boolean isPaid = false;

    private String nextWeek;

    @NonNull
    private String patients;



}
