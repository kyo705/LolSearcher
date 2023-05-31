package com.lolsearcher.user.session;

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

    public List<String> findAllById(Long userId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return sessionRepository.findAllSessions(authentication.getName());
    }

    public String cutOneSessionFromCurrentUser(Long userId, String currentSessionId, String cutSessionId){

        sessionRepository.deleteOneSession(cutSessionId);

        return cutSessionId;
    }
}
