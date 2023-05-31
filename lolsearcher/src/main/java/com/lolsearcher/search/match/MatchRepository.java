package com.lolsearcher.search.match;

import com.lolsearcher.search.match.entity.Match;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {

    Optional<Match> findById(String matchId);

    List<Match> findMatches(String summonerId, Integer queueId, Integer championId, int count, int offset);
}
