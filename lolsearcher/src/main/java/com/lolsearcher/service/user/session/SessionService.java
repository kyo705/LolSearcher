package com.lolsearcher.service.user.session;

import com.lolsearcher.model.request.user.session.RequestCutSessionDto;
import com.lolsearcher.repository.session.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public List<String> findAllSessionsFromCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return sessionRepository.findAllSessions(authentication.getName());
    }

    public String cutOneSessionFromCurrentUser(RequestCutSessionDto request){

        String cutSessionId = request.getCutSessionId();

        sessionRepository.deleteOneSession(cutSessionId);

        return cutSessionId;
    }
}
