package com.pablogb.psychologger.controller.view;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionContextView {
    @NonNull
    private Long patientId;
    @NonNull
    private SessionView sessionView;
}
