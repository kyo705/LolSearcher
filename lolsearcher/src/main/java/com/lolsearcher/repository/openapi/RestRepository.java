package com.lolsearcher.repository.openapi;

import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.summoner.Summoner;

import java.util.List;

public interface RestRepository {

	Summoner getSummonerById(String id);

	Summoner getSummonerByName(String name);

	Rank getRank(String id, String type, int season);

	List<Rank> getRanks(String id, int season);

	List<String> getMatchIds(String summonerId, int start, int count);

	Match getMatch(String matchId);
}
