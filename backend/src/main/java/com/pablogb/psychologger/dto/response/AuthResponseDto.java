package com.pablogb.psychologger.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isAdmin;
    private Boolean isTherapist;
    private Integer orgId;
}