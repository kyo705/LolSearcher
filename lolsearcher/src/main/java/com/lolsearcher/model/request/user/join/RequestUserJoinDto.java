package com.lolsearcher.model.request.user.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Builder
@AllArgsConstructor
@Data
public class RequestUserJoinDto {

    @Email
    private final String email;
    @NotEmpty
    private final String password;
    @NotEmpty
    private final String username;

    public RequestUserJoinDto(){
        this.email = "";
        this.password = "";
        this.username = "";
    }
}
