package com.lolsearcher.domain.Dto;


public class MostChampBuilder {

	private String championid;
	private double avgcs;
	private double avgkill;
	private double avgdeath;
	private double avgassist;
	private long totalgame;
	private int totalwin;
	
	public MostChampBuilder setChampionid(String championid) {
		this.championid = championid;
		return this;
	}
	public MostChampBuilder setAvgcs(double avgcs) {
		this.avgcs = avgcs;
		return this;
	}
	public MostChampBuilder setAvgkill(double avgkill) {
		this.avgkill = avgkill;
		return this;
	}
	public MostChampBuilder setAvgdeath(double avgdeath) {
		this.avgdeath = avgdeath;
		return this;
	}
	public MostChampBuilder setAvgassist(double avgassist) {
		this.avgassist = avgassist;
		return this;
	}
	public MostChampBuilder setTotalgame(long totalgame) {
		this.totalgame = totalgame;
		return this;
	}
	public MostChampBuilder setTotalwin(int totalwin) {
		this.totalwin = totalwin;
		return this;
	}
	
	public MostChampDto build() {
		return new MostChampDto(championid, avgcs, avgkill, avgdeath, avgassist, totalgame,totalwin);
	}
}
