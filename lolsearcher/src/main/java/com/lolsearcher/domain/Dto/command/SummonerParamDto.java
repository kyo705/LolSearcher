package com.lolsearcher.domain.Dto.command;

public class SummonerParamDto {

	private String name;
	private String summonerid;
	private String champion;
	private int mostgametype;
	private int matchgametype;
	private int count;
	private int season;
	private boolean renew;
	
	public SummonerParamDto() {
		this.name="";
		this.summonerid = "";
		this.champion = "all";
		this.mostgametype = -1;
		this.matchgametype = -1;
		this.count = 100;
		this.season = 12;
		this.renew = false;
	}
	
	public SummonerParamDto(String name, String summonerid, String champion, int mostgametype,
			int matchgametype, int count, int season, boolean renew) {
		
		this.name = name;
		this.summonerid = summonerid;
		this.champion = champion;
		this.mostgametype = mostgametype;
		this.matchgametype = matchgametype;
		this.count = count;
		this.season = season;
		this.renew = renew;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSummonerid() {
		return summonerid;
	}
	public void setSummonerid(String summonerid) {
		this.summonerid = summonerid;
	}
	public String getChampion() {
		return champion;
	}
	public void setChampion(String champion) {
		this.champion = champion;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}
	public boolean isRenew() {
		return renew;
	}
	public void setRenew(boolean renew) {
		this.renew = renew;
	}

	public int getMostgametype() {
		return mostgametype;
	}

	public void setMostgametype(int mostgametype) {
		this.mostgametype = mostgametype;
	}

	public int getMatchgametype() {
		return matchgametype;
	}

	public void setMatchgametype(int matchgametype) {
		this.matchgametype = matchgametype;
	}

	@Override
	public String toString() {
		return "summonerparamDto [name=" + name + ", summonerid=" + summonerid + ", champion=" + champion
				+ ", mostgametype=" + mostgametype + ", matchgametype=" + matchgametype + ", count=" + count
				+ ", season=" + season + ", renew=" + renew + "]";
	}
	
	
}
