package com.lolsearcher.model.output.front.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Data
public class InGameParticipantDto implements Serializable {

	private long championId;
	private long profileIconId;
	private boolean bot;
	private long teamId;
	private String summonerName;
	private String summonerId;
	private long spell1Id;
	private long spell2Id;
	private InGamePerksDto perks;

}
