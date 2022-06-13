package com.lolsearcher.domain.entity.ingame;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.lolsearcher.domain.Dto.ingame.BannedChampionDto;
import com.lolsearcher.domain.Dto.ingame.CurrentGameParticipantDto;
import com.lolsearcher.domain.Dto.ingame.InGameDto;



@Entity
public class InGame {
	
	@Id
	private long gameId;
	
	private String gameType;
	
	private long gameStartTime;
	
	private long mapId;
	
	private long gameLength;
	
	private String platformId;
	
	private String gameMode;
	
	private long gameQueueConfigId;
	
	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "ingame", cascade = CascadeType.ALL)
	private List<BannedChampion> bannedChampions = new ArrayList<>();
	
	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "ingame", cascade = CascadeType.ALL)
	private List<CurrentGameParticipant> participants = new ArrayList<>();
	
	public InGame() {}
	
	public void changeDtoToEntity(InGameDto ingameDto) {
		
		this.gameId = ingameDto.getGameId();
		this.gameType = ingameDto.getGameType();
		this.gameStartTime = ingameDto.getGameStartTime();
		this.mapId = ingameDto.getMapId();
		this.gameLength = ingameDto.getGameLength();
		this.platformId = ingameDto.getPlatformId();
		this.gameMode = ingameDto.getGameMode();
		this.gameQueueConfigId = ingameDto.getGameQueueConfigId();
		
		for(BannedChampionDto bannedchampDto : ingameDto.getBannedChampions()) {
			
			BannedChampion bannedchamp = new BannedChampion();
			bannedchamp.changeDtoToEntity(bannedchampDto, ingameDto.getGameId());
			
			this.addBannedChampion(bannedchamp);
		}
		
		for(CurrentGameParticipantDto participantDto : ingameDto.getParticipants()) {
			
			CurrentGameParticipant participant = new CurrentGameParticipant();
			participant.changeDtoToEntity(participantDto, ingameDto.getGameId()); 
			
			this.addCurrentGameParticipant(participant);
		}
	}
	
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
	public long getGameQueueConfigId() {
		return gameQueueConfigId;
	}
	public void setGameQueueConfigId(long gameQueueConfigId) {
		this.gameQueueConfigId = gameQueueConfigId;
	}
	
	public List<BannedChampion> getBannedChampions() {
		return bannedChampions;
	}
	public void setBannedChampions(List<BannedChampion> bannedChampions) {
		this.bannedChampions = bannedChampions;
	}
	
	public List<CurrentGameParticipant> getParticipants() {
		return participants;
	}
	public void setParticipants(List<CurrentGameParticipant> participants) {
		this.participants = participants;
	}
	
	public void addBannedChampion(BannedChampion bannedChamp) {
		this.bannedChampions.add(bannedChamp);
		bannedChamp.setIngame(this);
	}
	
	public void addCurrentGameParticipant(CurrentGameParticipant participant) {
		this.participants.add(participant);
		participant.setIngame(this);
	}
}
