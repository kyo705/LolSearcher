package com.lolsearcher.controller.user;

import com.lolsearcher.constant.enumeration.LoginSecurityPolicyStatus;
import com.lolsearcher.model.request.user.security.RequestLoginSecurityPolicyDto;
import com.lolsearcher.model.response.front.user.ResponseUserSecurityLevelChangeDto;
import com.lolsearcher.service.user.security.UserLoginSecurityPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserSecurityController {

    private final UserLoginSecurityPolicyService userSecurityService;

    @PatchMapping("/user/security/level")
    public ResponseUserSecurityLevelChangeDto changeUserSecurityPolicy(@RequestBody RequestLoginSecurityPolicyDto request){

        validateRequest(request);
        int beforeSecurityLevel = userSecurityService.changeLoginSecurityPolicy(request);

        return createResponseDto(beforeSecurityLevel, request.getLoginSecurityPolicyLevel());
    }

    private void validateRequest(RequestLoginSecurityPolicyDto request) {

        int requestSecurityLevel = request.getLoginSecurityPolicyLevel();

        if(LoginSecurityPolicyStatus.valueOfLevel(requestSecurityLevel) == null){
            String errorMessage = String.format("USER_SECURITY_LEVEL : %s 는 존재하지 않습니다.", requestSecurityLevel);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private ResponseUserSecurityLevelChangeDto createResponseDto(int beforeSecurityLevel, int afterSecurityLevel) {

        LoginSecurityPolicyStatus beforeLevel = LoginSecurityPolicyStatus.valueOfLevel(beforeSecurityLevel);
        LoginSecurityPolicyStatus afterLevel = LoginSecurityPolicyStatus.valueOfLevel(afterSecurityLevel);

        log.info("유저 보안 레벨이 {} -> {} 로 변경됨.", beforeLevel.name(), afterLevel.name() );

        return new ResponseUserSecurityLevelChangeDto(beforeLevel, afterLevel);
    }
}
