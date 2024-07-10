package com.pablogb.psychologger.dto.view;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateView {

    private Long id;

    @NonNull
    private String sessionDate;

    @NotBlank(message = "Subject cannot be blank")
    @NonNull
    private String themes;

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
