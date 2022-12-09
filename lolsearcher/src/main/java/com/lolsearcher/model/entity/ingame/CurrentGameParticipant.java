package com.lolsearcher.model.entity.ingame;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.lolsearcher.model.dto.ingame.CurrentGameParticipantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
@Entity
public class CurrentGameParticipant {

	@EmbeddedId
	private CurrentParticipantCompKey ck;
	
	private int numb;
	private long championId;
	private long profileIconId;
	private boolean bot;
	private long teamId;
	private String summonerName;
	private long spell1Id;
	private long spell2Id;
	
	private long mainPerk1;
	private long mainPerk2;
	private long mainPerk3;
	private long mainPerk4;

	private long subPerk1;
	private long subPerk2;

	private long statPerk1;
	private long statPerk2;
	private long statPerk3;
	
	private long perkStyle;
	private long perkSubStyle;
	
	@MapsId("gameId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="gameId")
	private InGame inGame;
	
	public CurrentGameParticipant(CurrentGameParticipantDto participant, long gameId) {
		this.ck = new CurrentParticipantCompKey(gameId, participant.getSummonerId());
		this.numb = participant.getNum();
		this.championId = participant.getChampionId();
		this.profileIconId = participant.getProfileIconId();
		this.bot = participant.isBot();
		this.teamId = participant.getTeamId();
		this.summonerName = participant.getSummonerName();
		this.spell1Id = participant.getSpell1Id();
		this.spell2Id = participant.getSpell2Id();
		
		List<Long> perkIds = participant.getPerks().getPerkIds();
		this.mainPerk1 = perkIds.get(0);
		this.mainPerk2 = perkIds.get(1);
		this.mainPerk3 = perkIds.get(2);
		this.mainPerk4 = perkIds.get(3);
		this.subPerk1 = perkIds.get(4);
		this.subPerk2 = perkIds.get(5);
		this.statPerk1 = perkIds.get(6);
		this.statPerk2 = perkIds.get(7);
		this.statPerk3 = perkIds.get(8);
		
		this.perkStyle = participant.getPerks().getPerkStyle();
		this.perkSubStyle = participant.getPerks().getPerkSubStyle();
	}

	public void setInGame(InGame inGame) {
		if(this.inGame !=null) {
			this.inGame.getParticipants().remove(this);
		}
		
		this.inGame = inGame;
		inGame.addCurrentGameParticipant(this);
	}
}
