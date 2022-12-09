package com.lolsearcher.model.entity.ingame;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.lolsearcher.model.dto.ingame.BannedChampionDto;
import com.lolsearcher.model.dto.ingame.CurrentGameParticipantDto;
import com.lolsearcher.model.dto.ingame.InGameDto;



@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
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
	@OneToMany(mappedBy = "inGame", cascade = CascadeType.ALL)
	private List<BannedChampion> bannedChampions = new ArrayList<>();
	
	
	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "inGame", cascade = CascadeType.ALL)
	private List<CurrentGameParticipant> participants = new ArrayList<>();
	
	public InGame(InGameDto ingameDto) {
		this.gameId = ingameDto.getGameId();
		this.gameType = ingameDto.getGameType();
		this.gameStartTime = ingameDto.getGameStartTime();
		this.mapId = ingameDto.getMapId();
		this.gameLength = ingameDto.getGameLength();
		this.platformId = ingameDto.getPlatformId();
		this.gameMode = ingameDto.getGameMode();
		this.gameQueueConfigId = ingameDto.getGameQueueConfigId();
		
		this.bannedChampions = new ArrayList<>();
		for(BannedChampionDto bannedchampDto : ingameDto.getBannedChampions()) {
			BannedChampion bannedchamp = new BannedChampion(bannedchampDto, ingameDto.getGameId());
			bannedchamp.setInGame(this);
		}
		
		this.participants = new ArrayList<>();
		for(CurrentGameParticipantDto participantDto : ingameDto.getParticipants()) {
			CurrentGameParticipant participant = new CurrentGameParticipant(participantDto, ingameDto.getGameId());
			participant.setInGame(this);
		}
	}

	public void addBannedChampion(BannedChampion bannedChamp) {
		this.bannedChampions.add(bannedChamp);
	}
	
	public void addCurrentGameParticipant(CurrentGameParticipant participant) {
		this.participants.add(participant);
	}
}
