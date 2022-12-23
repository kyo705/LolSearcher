package com.lolsearcher.repository.match;

import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.match.PerkStats;

import java.util.List;

public interface MatchRepository {
    Match findMatchById(String matchId);

    void saveMatch(Match match);

    List<Match> findMatches(String summonerId, int gameType, String champion, int count);

    PerkStats findPerkStats(short defense, short flex, short offense);
}
