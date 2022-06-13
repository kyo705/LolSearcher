package com.lolsearcher.domain.entity.ingame;

import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.lolsearcher.domain.Dto.ingame.CurrentGameParticipantDto;

@Entity
public class CurrentGameParticipant {

	@EmbeddedId
	private CurrentParticipantCompKey ck;
	
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
	@ManyToOne
	@JoinColumn(name="gameId")
	private InGame ingame;
	
	public CurrentGameParticipant() {}
	
	public void changeDtoToEntity(CurrentGameParticipantDto participant, long gameId) {
		this.ck = new CurrentParticipantCompKey(gameId, participant.getSummonerId());
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

	public CurrentParticipantCompKey getCk() {
		return ck;
	}

	public void setCk(CurrentParticipantCompKey ck) {
		this.ck = ck;
	}

	public long getChampionId() {
		return championId;
	}
	public void setChampionId(long championId) {
		this.championId = championId;
	}
	public long getProfileIconId() {
		return profileIconId;
	}
	public void setProfileIconId(long profileIconId) {
		this.profileIconId = profileIconId;
	}
	public boolean isBot() {
		return bot;
	}
	public void setBot(boolean bot) {
		this.bot = bot;
	}
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	public String getSummonerName() {
		return summonerName;
	}
	public void setSummonerName(String summonerName) {
		this.summonerName = summonerName;
	}
	public long getSpell1Id() {
		return spell1Id;
	}
	public void setSpell1Id(long spell1Id) {
		this.spell1Id = spell1Id;
	}
	public long getSpell2Id() {
		return spell2Id;
	}
	public void setSpell2Id(long spell2Id) {
		this.spell2Id = spell2Id;
	}

	public InGame getIngame() {
		return ingame;
	}

	public void setIngame(InGame ingame) {
		this.ingame = ingame;
	}

	public long getMainPerk1() {
		return mainPerk1;
	}

	public void setMainPerk1(long mainPerk1) {
		this.mainPerk1 = mainPerk1;
	}

	public long getMainPerk2() {
		return mainPerk2;
	}

	public void setMainPerk2(long mainPerk2) {
		this.mainPerk2 = mainPerk2;
	}

	public long getMainPerk3() {
		return mainPerk3;
	}

	public void setMainPerk3(long mainPerk3) {
		this.mainPerk3 = mainPerk3;
	}

	public long getMainPerk4() {
		return mainPerk4;
	}

	public void setMainPerk4(long mainPerk4) {
		this.mainPerk4 = mainPerk4;
	}

	public long getSubPerk1() {
		return subPerk1;
	}

	public void setSubPerk1(long subPerk1) {
		this.subPerk1 = subPerk1;
	}

	public long getSubPerk2() {
		return subPerk2;
	}

	public void setSubPerk2(long subPerk2) {
		this.subPerk2 = subPerk2;
	}

	public long getStatPerk1() {
		return statPerk1;
	}

	public void setStatPerk1(long statPerk1) {
		this.statPerk1 = statPerk1;
	}

	public long getStatPerk2() {
		return statPerk2;
	}

	public void setStatPerk2(long statPerk2) {
		this.statPerk2 = statPerk2;
	}

	public long getStatPerk3() {
		return statPerk3;
	}

	public void setStatPerk3(long statPerk3) {
		this.statPerk3 = statPerk3;
	}

	public long getPerkStyle() {
		return perkStyle;
	}

	public void setPerkStyle(long perkStyle) {
		this.perkStyle = perkStyle;
	}

	public long getPerkSubStyle() {
		return perkSubStyle;
	}

	public void setPerkSubStyle(long perkSubStyle) {
		this.perkSubStyle = perkSubStyle;
	}
	
	
}
