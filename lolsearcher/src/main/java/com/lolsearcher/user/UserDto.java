package com.lolsearcher.user;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private Role role;
    private LoginSecurityState loginSecurity;
    private LocalDateTime lastLoginTimestamp;

}
