package com.lolsearcher.domain.Dto;

import com.lolsearcher.domain.entity.Summoner;

public class SummonerDto {

	private String summonerid;
	private String puuid;
	private String name;
	private int profileIconId;
	private long summonerLevel;
	
	public SummonerDto() {
		// TODO Auto-generated constructor stub
	}
	
	public SummonerDto(Summoner summoner) {
		this.summonerid = summoner.getId();
		this.puuid = summoner.getPuuid();
		this.name = summoner.getName();
		this.profileIconId = summoner.getProfileIconId();
		this.summonerLevel = summoner.getSummonerLevel();
	}
	
	public String getSummonerid() {
		return summonerid;
	}
	public void setSummonerid(String summonerid) {
		this.summonerid = summonerid;
	}
	public String getPuuid() {
		return puuid;
	}
	public void setPuuid(String puuid) {
		this.puuid = puuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getProfileIconId() {
		return profileIconId;
	}

	public void setProfileIconId(int profileIconId) {
		this.profileIconId = profileIconId;
	}

	public long getSummonerLevel() {
		return summonerLevel;
	}

	public void setSummonerLevel(long summonerLevel) {
		this.summonerLevel = summonerLevel;
	}
	
}
