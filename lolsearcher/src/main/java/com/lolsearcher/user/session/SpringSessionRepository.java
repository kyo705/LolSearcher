package com.lolsearcher.user.session;

import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class SpringSessionRepository implements SessionRepository{

    private final FindByIndexNameSessionRepository<? extends Session> repository;

    @Override
    public List<String> findAllSessions(String principal) {

        return new ArrayList<>(repository.findByPrincipalName(principal).keySet());
    }

    @Override
    public void deleteOneSession(String sessionId) {

        repository.deleteById(sessionId);
    }
}
