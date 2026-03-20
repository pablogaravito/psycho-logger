package com.pablogb.psychologger.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean isTherapist;
    private Boolean isAdmin;
    private Boolean isActive;
}
