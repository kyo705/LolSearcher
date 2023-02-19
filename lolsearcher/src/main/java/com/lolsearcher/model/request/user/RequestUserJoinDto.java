package com.lolsearcher.model.request.user;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class RequestUserJoinDto {

    @Email
    private final String email;
    private final String password;
    private final String username;

    public RequestUserJoinDto(){
        this.email = "";
        this.password = "";
        this.username = "";
    }
}
