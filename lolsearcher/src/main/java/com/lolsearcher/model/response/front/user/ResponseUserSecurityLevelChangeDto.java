package com.lolsearcher.model.response.front.user;

import com.lolsearcher.constant.enumeration.LoginSecurityPolicyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ResponseUserSecurityLevelChangeDto {

    private final LoginSecurityPolicyStatus beforeSecurityLevel;
    private final LoginSecurityPolicyStatus afterSecurityLevel;

    public ResponseUserSecurityLevelChangeDto(){
        beforeSecurityLevel = LoginSecurityPolicyStatus.NONE;
        afterSecurityLevel = LoginSecurityPolicyStatus.NONE;
    }
}
