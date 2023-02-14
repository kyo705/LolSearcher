package com.lolsearcher.repository.search.match;

import com.lolsearcher.model.entity.match.Match;

import java.util.List;

public interface MatchRepository {

    Match findMatchByGameId(String matchId);

    List<Match> findMatches(String summonerId, int queueId, int championId, int count);
}
