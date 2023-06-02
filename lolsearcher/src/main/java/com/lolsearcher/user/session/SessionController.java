package com.lolsearcher.user.session;

import com.lolsearcher.user.ResponseSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lolsearcher.user.session.SessionConstant.USER_SESSION_URI;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(USER_SESSION_URI)
@RestController
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public List<String> findAllById(@PathVariable Long userId){

        return sessionService.findAllById(userId);
    }

    @DeleteMapping
    public ResponseSuccessDto delete(@PathVariable Long userId, @RequestBody SessionDeleteRequest request) {

        return sessionService.cutOneSessionFromCurrentUser(userId, request.getCutSessionId());
    }
}
