package com.lolsearcher.restapi;

import java.util.List;
import java.util.Set;

import com.lolsearcher.domain.entity.Match;
import com.lolsearcher.domain.entity.Rank;
import com.lolsearcher.domain.entity.Summoner;

public interface RiotRestAPI {
	
	public Summoner getSummoner(String summonername);
	public List<String> listofmatch(String puuid, int queue, String type, int start, int count, String lastmatchid);
	public Match getmatch(String matchid);
	public Set<Rank> getLeague(String summonerid);
}
