package com.lolsearcher.repository.restapirepository;

import java.util.List;

import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.rank.Rank;

public interface RestRepository {

	public Summoner getSummonerById(String id);

	public Summoner getSummonerByName(String name);

	public Rank getRank(String id, String type, int season);

	public List<Rank> getRanks(String id, int season);

	public List<String> getMatchIds(String summonerId, int start, int count);

	public Match getMatch(String matchId);

	public void setMatch(Match match);

}
