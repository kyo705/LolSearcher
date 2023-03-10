package com.lolsearcher.repository.session;

import java.util.List;

public interface SessionRepository {

    List<String> findAllSessions(String principal);
    void deleteOneSession(String sessionId);
}
