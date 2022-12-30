package com.lolsearcher.model.dto.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Data
public class CurrentGameParticipantDto implements Serializable {
	
	private int num;
	private long championId;
	private PerksDto perks;
	private long profileIconId;
	private boolean bot;
	private long teamId;
	private String summonerName;
	private String summonerId;
	private long spell1Id;
	private long spell2Id;

}
