package com.lolsearcher.restapi;

import java.util.List;
import com.lolsearcher.domain.entity.Match;
import com.lolsearcher.domain.entity.Rank;
import com.lolsearcher.domain.entity.Summoner;

public interface RiotRestAPI {
	
	public Summoner getSummoner(String summonername);
	public List<String> listofmatch(String puuid, int queue, String type, int start, int count, String lastmatchid);
	public Match getmatch(String matchid);
	public List<Rank> getLeague(String summonerid);
}
