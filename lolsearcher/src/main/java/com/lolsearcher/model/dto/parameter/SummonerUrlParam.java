package com.lolsearcher.model.dto.parameter;

import lombok.*;

@Builder
@AllArgsConstructor
@Data
public class SummonerUrlParam {
	private String name;
	private String summonerId;
	private String champion;
	private int mostGameType;
	private int matchGameType;
	private int count;
	private int season;
	private boolean renew;
	
	public SummonerUrlParam() {
		this.name="";
		this.summonerId = "";
		this.champion = "all";
		this.mostGameType = -1;
		this.matchGameType = -1;
		this.count = 100;
		this.season = 12;
		this.renew = false;
	}
}
