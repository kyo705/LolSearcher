package com.lolsearcher.model.response.front.ingame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InGameDto implements Serializable {

	private long gameId;
	private String gameType;
	private long gameStartTime;
	private long mapId;
	private long gameLength;
	private String platformId;
	private String gameMode;
	private List<BannedChampionDto> bannedChampions = new ArrayList<>();
	private long gameQueueConfigId;
	private List<CurrentGameParticipantDto> participants = new ArrayList<>();
}

