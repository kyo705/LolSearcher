package com.lolsearcher.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static com.lolsearcher.user.UserConstant.EMAIL_REGEX;

@Builder
@AllArgsConstructor
@Data
public class UserCreateRequest {

    @NotEmpty
    @Email(regexp = EMAIL_REGEX)
    private final String email;
    @NotBlank @Size(min = 9)
    private final String password;
    @NotBlank
    private final String username;

    public UserCreateRequest(){
        this.email = "";
        this.password = "";
        this.username = "";
    }
}
