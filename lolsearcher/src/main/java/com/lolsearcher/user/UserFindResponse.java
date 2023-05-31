package com.lolsearcher.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Setter
@Getter
public class UserFindResponse {

    private boolean isExisted;

    public UserFindResponse() {}
}
