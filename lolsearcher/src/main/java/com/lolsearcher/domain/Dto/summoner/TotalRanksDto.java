package com.lolsearcher.domain.Dto.summoner;

public class TotalRanksDto {

	private RankDto solorank;
	private RankDto teamrank;
	
	public RankDto getSolorank() {
		return solorank;
	}
	public void setSolorank(RankDto solorank) {
		this.solorank = solorank;
	}
	public RankDto getTeamrank() {
		return teamrank;
	}
	public void setTeamrank(RankDto teamrank) {
		this.teamrank = teamrank;
	}
}
