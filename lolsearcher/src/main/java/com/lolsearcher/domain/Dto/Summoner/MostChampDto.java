package com.lolsearcher.domain.Dto.Summoner;

public class MostChampDto {

	private String championid;
	private String championname;
	private double avgcs;
	private double avgkill;
	private double avgdeath;
	private double avgassist;
	private long totalgame;
	private int totalwin;
	
	public MostChampDto() {
		
	}
	public MostChampDto(String championid, double avgcs, double avgkill, double avgdeath, double avgassist, long totalgame,
			int totalwin) {
		super();
		this.championid = championid;
		this.avgcs = avgcs;
		this.avgkill = avgkill;
		this.avgdeath = avgdeath;
		this.avgassist = avgassist;
		this.totalgame = totalgame;
		this.totalwin = totalwin;
	}
	
	public String getChampionid() {
		return championid;
	}
	public void setChampionid(String championid) {
		this.championid = championid;
	}
	public double getAvgcs() {
		return avgcs;
	}
	public void setAvgcs(double avgcs) {
		this.avgcs = avgcs;
	}
	public double getAvgkill() {
		return avgkill;
	}
	public void setAvgkill(double avgkill) {
		this.avgkill = avgkill;
	}
	public double getAvgdeath() {
		return avgdeath;
	}
	public void setAvgdeath(double avgdeath) {
		this.avgdeath = avgdeath;
	}
	public double getAvgassist() {
		return avgassist;
	}
	public void setAvgassist(double avgassist) {
		this.avgassist = avgassist;
	}
	public long getTotalgame() {
		return totalgame;
	}
	public void setTotalgame(long totalgame) {
		this.totalgame = totalgame;
	}
	public int getTotalwin() {
		return totalwin;
	}
	public void setTotalwin(int totalwin) {
		this.totalwin = totalwin;
	}
}