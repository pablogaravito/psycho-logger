package com.pablogb.psychologger.controller.view.dto;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.utils.DateUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
