package com.lolsearcher.model.dto.ingame;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.model.entity.ingame.BannedChampion;
import com.lolsearcher.model.entity.ingame.CurrentGameParticipant;
import com.lolsearcher.model.entity.ingame.InGame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@AllArgsConstructor
@Data
public class InGameDto {

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
	
	public InGameDto(InGame ingame) {
		this.gameId = ingame.getGameId();
		this.gameType = ingame.getGameType();
		this.gameStartTime = ingame.getGameStartTime();
		this.mapId = ingame.getMapId();
		this.gameLength = ingame.getGameLength();
		this.platformId = ingame.getPlatformId();
		this.gameMode = ingame.getGameMode();
		this.gameQueueConfigId = ingame.getGameQueueConfigId();
		
		for(BannedChampion bannedChampion : ingame.getBannedChampions()) {
			bannedChampions.add(new BannedChampionDto(bannedChampion));
		}
		
		for(CurrentGameParticipant participant : ingame.getParticipants()) {
			
			participants.add(new CurrentGameParticipantDto(participant));
		 }
		
	}
}

