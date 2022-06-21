package com.lolsearcher.domain.Dto.command;

public class MostChampParamDtoBuilder {

	private String summonerid;
	private int gamequeue;
	private int season;
	
	public MostChampParamDtoBuilder() {
		this.season = 12;
	}
	
	public MostchampParamDto build() {
		return new MostchampParamDto(summonerid, gamequeue, season);
	}
	
	public MostChampParamDtoBuilder setSummonerid(String summonerid) {
		this.summonerid = summonerid;
		return this;
	}
	
	public MostChampParamDtoBuilder setGameQueue(int gamequeue) {
		this.gamequeue = gamequeue;
		return this;
	}
	
	public MostChampParamDtoBuilder setSeason(int season) {
		this.season = season;
		return this;
	}
}
