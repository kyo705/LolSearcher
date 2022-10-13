package com.lolsearcher.domain.Dto.summoner;

import java.util.List;

import com.lolsearcher.domain.entity.summoner.match.Match;

public class RecentMatchesDto {

	private List<Match> matches;
	private List<String> failMatchIds;
	
	public RecentMatchesDto() {}
	
	public RecentMatchesDto(List<Match> matches, List<String> failMatchIds) {
		this.matches = matches;
		this.failMatchIds = failMatchIds;
	}
	
	public List<Match> getMatches() {
		return matches;
	}
	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}
	public List<String> getFailMatchIds() {
		return failMatchIds;
	}
	public void setFailMatchIds(List<String> failMatchIds) {
		this.failMatchIds = failMatchIds;
	}
	
	
}
