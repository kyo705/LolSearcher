package com.lolsearcher.user.session;

import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class SpringSessionRepository implements SessionRepository{

    private final FindByIndexNameSessionRepository<? extends Session> repository;

    @Override
    public List<String> findAllSessions(String principal) {

        return repository.findByPrincipalName(principal)
                .entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isExpired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOneSession(String sessionId) {

        repository.deleteById(sessionId);
    }
}
