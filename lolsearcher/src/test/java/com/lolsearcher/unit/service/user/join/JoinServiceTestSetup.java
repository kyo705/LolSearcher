package com.lolsearcher.unit.service.user.join;

import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.RequestUserJoinDto;

public class JoinServiceTestSetup {
    protected static RequestUserJoinDto getRequestUserJoinDto() {

        return RequestUserJoinDto.builder()
                .email("email@gmail.com")
                .password("password")
                .username("닉네임")
                .build();
    }

    protected static LolSearcherUser getLolSearcherUser(String email) {

        return LolSearcherUser.builder()
                .email(email)
                .password("password")
                .username("이미 존재하는 유저")
                .role("역할")
                .build();
    }
}
