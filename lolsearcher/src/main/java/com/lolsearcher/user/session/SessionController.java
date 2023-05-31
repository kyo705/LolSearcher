package com.lolsearcher.user.session;

import com.lolsearcher.user.ResponseSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/user/{userId}/sessions")
    public List<String> findAllById(@PathVariable Long userId){

        return sessionService.findAllById(userId);
    }

    @DeleteMapping("/user/{id}/session")
    public ResponseSuccessDto delete(
            @PathVariable Long userId,
            @RequestBody SessionDeleteRequest request,
            HttpSession session
    ) {

        String currentSessionId = session.getId();
        String removedSessionId = sessionService.cutOneSessionFromCurrentUser(userId, currentSessionId, request.getCutSessionId());

        String message = String.format("현재 세션 : {}에서 특정 세션 : {} 를 삭제함", currentSessionId, removedSessionId);
        log.info(message);

        return new ResponseSuccessDto(message);
    }
}
