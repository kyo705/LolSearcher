package com.lolsearcher.controller.user;

import com.lolsearcher.model.request.user.session.RequestCutSessionDto;
import com.lolsearcher.model.response.front.user.ResponseRemovedSessionDto;
import com.lolsearcher.service.user.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserSessionController {

    private final SessionService sessionService;

    @GetMapping("/user/security/sessions")
    public List<String> getAllSessionFromCurrentUser(){

        return sessionService.findAllSessionsFromCurrentUser();
    }

    @DeleteMapping("/user/security/session")
    public ResponseRemovedSessionDto removeOneSessionFromCurrentUser(
            HttpSession session,
            @RequestBody RequestCutSessionDto request){

        String currentSessionId = session.getId();
        String removedSessionId = sessionService.cutOneSessionFromCurrentUser(request);

        return createResponseRemovedSessionDto(currentSessionId, removedSessionId);
    }

    private ResponseRemovedSessionDto createResponseRemovedSessionDto(String currentSessionId, String removedSessionId) {

        log.info("현재 세션 : {}에서 특정 세션 : {} 를 삭제함", currentSessionId, removedSessionId);

        return new ResponseRemovedSessionDto(currentSessionId, removedSessionId);
    }
}
