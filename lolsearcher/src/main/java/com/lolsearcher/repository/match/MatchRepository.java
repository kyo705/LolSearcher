package com.lolsearcher.repository.match;

import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.match.PerkStats;

import java.util.List;

public interface MatchRepository {

    void saveMatch(Match match);

    Match findMatchByGameId(String matchId);

    List<Match> findMatches(String summonerId, int queueId, int championId, int count);

    PerkStats findPerkStats(short defense, short flex, short offense);
}
