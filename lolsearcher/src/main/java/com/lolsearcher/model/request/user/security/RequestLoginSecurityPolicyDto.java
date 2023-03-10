package com.lolsearcher.model.request.user.security;

import lombok.Data;

@Data
public class RequestLoginSecurityPolicyDto {

    private final int loginSecurityPolicyLevel;

    public RequestLoginSecurityPolicyDto(){
        loginSecurityPolicyLevel = 0;
    }
}
