package com.lolsearcher.model.request.user;

import lombok.Data;

@Data
public class JwtJoinAuthentication implements JoinAuthentication {

    private final String token;
    private final int randomNum;

    @Override
    public Object getUserInfo() {
        return token;
    }

    @Override
    public int getRandomNum() {
        return this.randomNum;
    }
}
