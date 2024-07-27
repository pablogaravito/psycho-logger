package com.pablogb.psychologger.dto.api;

import com.pablogb.psychologger.dto.view.SessionCreateView;
import com.pablogb.psychologger.util.CommonUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSessionDto {
    private Long id;

    @NonNull
    private LocalDate sessionDate;

    @NotBlank(message = "Themes cannot be blank")
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
    private List<Long> patients;

    public static CreateSessionDto createFromSessionCreateView(SessionCreateView sessionCreateView) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return CreateSessionDto.builder()
                .id(sessionCreateView.getId())
                .sessionDate(LocalDate.parse(sessionCreateView.getSessionDate(), format))
                .themes(sessionCreateView.getThemes())
                .content(sessionCreateView.getContent())
                .isImportant(sessionCreateView.getIsImportant())
                .isPaid(sessionCreateView.getIsPaid())
                .nextWeek(sessionCreateView.getNextWeek())
                .patients(CommonUtils.getPatientIds(sessionCreateView.getPatients()))
                .build();
    }
}
