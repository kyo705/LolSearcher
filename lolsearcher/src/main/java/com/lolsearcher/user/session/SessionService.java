package com.lolsearcher.user.session;

import com.lolsearcher.login.LolsearcherUserDetails;
import com.lolsearcher.user.ResponseSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public List<String> findAllById(Long userId){

        return sessionRepository.findAllSessions(getUserDetails(userId).getUsername());
    }

    public ResponseSuccessDto cutOneSessionFromCurrentUser(Long userId, String cutSessionId){

        return sessionRepository.findAllSessions(getUserDetails(userId).getUsername())
                .stream()
                .filter(sessionId -> {
                    System.out.println(sessionId + " " + cutSessionId);
                    return sessionId.equals(cutSessionId);
                })
                .peek(sessionRepository::deleteOneSession)
                .map(sessionId -> new ResponseSuccessDto(TRUE, String.format("Session Id : %s is removed successfully!!", sessionId)))
                .findAny()
                .orElseGet(() -> new ResponseSuccessDto(FALSE, String.format("Session Id : %s is not existing in current user's sessions", cutSessionId)));
    }

    private LolsearcherUserDetails getUserDetails(Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LolsearcherUserDetails details = (LolsearcherUserDetails) authentication.getPrincipal();

        if(userId != details.getId()) {
            throw new IllegalArgumentException("path param userId is not session user's id");
        }
        return details;
    }
}
