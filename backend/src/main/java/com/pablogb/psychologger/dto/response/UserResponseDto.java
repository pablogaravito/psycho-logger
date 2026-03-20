package com.pablogb.psychologger.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isTherapist;
    private Boolean isAdmin;
    private Boolean isActive;
    private LocalDateTime createdAt;
}