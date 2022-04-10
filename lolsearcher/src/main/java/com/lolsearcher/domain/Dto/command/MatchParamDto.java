package com.lolsearcher.domain.Dto.command;

public class MatchParamDto {

	private String name;
	private String summonerid;
	private String champion;
	private int gametype;
	private int count;
	
	public MatchParamDto() {
		name = "";
		summonerid = "";
		champion = "all";
		gametype = -1;
		count = 1;
	}
	
	public MatchParamDto(String name, String summonerid, String champion, int gametype, int count) {
		super();
		this.name = name;
		this.summonerid = summonerid;
		this.champion = champion;
		this.gametype = gametype;
		this.count = count;
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
	public int getGametype() {
		return gametype;
	}
	public void setGametype(int gametype) {
		this.gametype = gametype;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
