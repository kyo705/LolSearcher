package com.lolsearcher.model.dto.ingame;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.model.entity.ingame.CurrentGameParticipant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class CurrentGameParticipantDto {
	
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
	private List<gameCustomizationObjectDto> gameCustomizationObjects;

	
	public CurrentGameParticipantDto(CurrentGameParticipant participant) {
		this.num = participant.getNumb();
		this.championId = participant.getChampionId();
		this.profileIconId = participant.getProfileIconId();
		this.bot = participant.isBot();
		this.teamId = participant.getTeamId();
		this.summonerName = participant.getSummonerName();
		this.summonerId = participant.getCk() .getSummonerId();
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
}
