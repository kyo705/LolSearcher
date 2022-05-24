package com.lolsearcher.domain.Dto.currentgame;

import java.util.List;



public class InGameDto {

	private long gameId;
	private String gameType;
	private long gameStartTime;
	private long mapId;
	private long gameLength;
	private String platformId;
	private String gameMode;
	private List<BannedChampionDto> bannedChampions;
	private long gameQueueConfigId;
	private List<CurrentGameParticipantDto> participants;
	
	public long getGameId() {
		return gameId;
	}

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public long getGameStartTime() {
		return gameStartTime;
	}

	public void setGameStartTime(long gameStartTime) {
		this.gameStartTime = gameStartTime;
	}

	public long getMapId() {
		return mapId;
	}

	public void setMapId(long mapId) {
		this.mapId = mapId;
	}

	public long getGameLength() {
		return gameLength;
	}

	public void setGameLength(long gameLength) {
		this.gameLength = gameLength;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getGameMode() {
		return gameMode;
	}

	public void setGameMode(String gameMode) {
		this.gameMode = gameMode;
	}

	public List<BannedChampionDto> getBannedChampions() {
		return bannedChampions;
	}

	public void setBannedChampions(List<BannedChampionDto> bannedChampions) {
		this.bannedChampions = bannedChampions;
	}

	public long getGameQueueConfigId() {
		return gameQueueConfigId;
	}

	public void setGameQueueConfigId(long gameQueueConfigId) {
		this.gameQueueConfigId = gameQueueConfigId;
	}

	public List<CurrentGameParticipantDto> getParticipants() {
		return participants;
	}

	public void setParticipants(List<CurrentGameParticipantDto> participants) {
		this.participants = participants;
	}
	
	
}

