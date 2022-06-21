package com.lolsearcher.domain.Dto.command;

public class MatchParamDtoBuilder {

	private String name;
	private String summonerid;
	private String champion;
	private int gametype;
	private int count;
	
	public MatchParamDtoBuilder() {
		name = "";
		summonerid = "";
		champion = "all";
		gametype = -1;
		count = 100;
	}
	
	public MatchParamDto build() {
		return new MatchParamDto(name, summonerid, champion, gametype, count);
	}
	
	public MatchParamDtoBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public MatchParamDtoBuilder setSummonerid(String summonerid) {
		this.summonerid = summonerid;
		return this;
	}
	public MatchParamDtoBuilder setChampion(String champion) {
		this.champion = champion;
		return this;
	}
	public MatchParamDtoBuilder setGametype(int gametype) {
		this.gametype = gametype;
		return this;
	}
	public MatchParamDtoBuilder setCount(int count) {
		this.count = count;
		return this;
	}
}
