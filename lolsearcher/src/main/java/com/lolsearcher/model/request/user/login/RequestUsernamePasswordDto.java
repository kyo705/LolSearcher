package com.lolsearcher.model.request.user.login;

import lombok.Data;

@Data
public class RequestUsernamePasswordDto {

    private final String email;
    private final String password;

    public RequestUsernamePasswordDto(){
        email = "";
        password = "";
    }
}
