package com.lolsearcher.model.request.user.join;

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
