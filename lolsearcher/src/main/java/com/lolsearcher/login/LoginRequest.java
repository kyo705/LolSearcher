package com.lolsearcher.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class LoginRequest {

    private final String email;
    private final String password;

    public LoginRequest(){
        email = "";
        password = "";
    }
}
