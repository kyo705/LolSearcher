package com.lolsearcher.model.request.user;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class RequestEmailCheckDto {

    @Email
    private final String email;

    public RequestEmailCheckDto(){
        this.email = "";
    }
}
