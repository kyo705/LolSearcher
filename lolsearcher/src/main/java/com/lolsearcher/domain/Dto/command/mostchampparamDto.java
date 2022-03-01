package com.lolsearcher.domain.Dto.command;

public class mostchampparamDto {

	private String summonerid;
	private int gamequeue;
	private int season;
	
	public mostchampparamDto() {
		//param값 초기화
		this.season = 12;
	}
	
	public mostchampparamDto(String summonerid, int gamequeue, int season) {
		this.summonerid = summonerid;
		this.gamequeue = gamequeue;
		this.season = season;
	}

	public int getGamequeue() {
		return gamequeue;
	}
	public void setGamequeue(int gamequeue) {
		this.gamequeue = gamequeue;
	}
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}

	public String getSummonerid() {
		return summonerid;
	}

	public void setSummonerid(String summonerid) {
		this.summonerid = summonerid;
	}
}

