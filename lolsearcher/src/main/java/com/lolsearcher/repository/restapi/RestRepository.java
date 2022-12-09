package com.lolsearcher.repository.restapi;

import java.util.List;

import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;

public interface RestRepository {

	Summoner getSummonerById(String id);

	Summoner getSummonerByName(String name);

	Rank getRank(String id, String type, int season);

	List<Rank> getRanks(String id, int season);

	List<String> getMatchIds(String summonerId, int start, int count);

	Match getMatch(String matchId);

	void setMatch(Match match);

}
