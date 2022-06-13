package com.lolsearcher.domain.Dto.ingame;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.domain.entity.ingame.CurrentGameParticipant;

public class CurrentGameParticipantDto {
	
	private long championId;
	private PerksDto perks;
	private long profileIconId;
	private boolean bot;
	private long teamId;
	private String summonerName;
	private String summonerId;
	private long spell1Id;
	private long spell2Id;
	private List<gameCustomizationObjectDto> gameCustomizationObjects;
	
	public CurrentGameParticipantDto() {}
	
	public CurrentGameParticipantDto(CurrentGameParticipant participant) {
		this.championId = participant.getChampionId();
		this.profileIconId = participant.getProfileIconId();
		this.bot = participant.isBot();
		this.teamId = participant.getTeamId();
		this.summonerName = participant.getSummonerName();
		this.summonerId = participant.getCk().getSummonerId();
		this.spell1Id = participant.getSpell1Id();
		this.spell2Id = participant.getSpell2Id();
		
		List<Long> perkIds = new ArrayList<>();
		perkIds.add(participant.getMainPerk1());
		perkIds.add(participant.getMainPerk2());
		perkIds.add(participant.getMainPerk3());
		perkIds.add(participant.getMainPerk4());
		perkIds.add(participant.getSubPerk1());
		perkIds.add(participant.getSubPerk2());
		perkIds.add(participant.getStatPerk1());
		perkIds.add(participant.getStatPerk2());
		perkIds.add(participant.getStatPerk3());
		
		PerksDto perkDto = new PerksDto();
		perkDto.setPerkIds(perkIds);
		perkDto.setPerkStyle(participant.getPerkStyle());
		perkDto.setPerkSubStyle(participant.getPerkSubStyle());
		
		this.perks = perkDto;
	}
	public long getChampionId() {
		return championId;
	}
	public void setChampionId(long championId) {
		this.championId = championId;
	}
	public PerksDto getPerks() {
		return perks;
	}
	public void setPerks(PerksDto perks) {
		this.perks = perks;
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
	public String getSummonerId() {
		return summonerId;
	}
	public void setSummonerId(String summonerId) {
		this.summonerId = summonerId;
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
	public List<gameCustomizationObjectDto> getGameCustomizationObjects() {
		return gameCustomizationObjects;
	}
	public void setGameCustomizationObjects(List<gameCustomizationObjectDto> gameCustomizationObjects) {
		this.gameCustomizationObjects = gameCustomizationObjects;
	}
	
	
}
