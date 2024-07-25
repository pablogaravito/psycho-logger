package com.pablogb.psychologger.dto.api;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {
    private Long id;
    private LocalDate sessionDate;
    private String themes;
    private String content;
    private Boolean isImportant;
    private Boolean isPaid;
    private String nextWeek;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
