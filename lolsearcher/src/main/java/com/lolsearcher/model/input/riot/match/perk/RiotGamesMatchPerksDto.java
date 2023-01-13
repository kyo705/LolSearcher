package com.lolsearcher.model.input.riot.match.perk;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RiotGamesMatchPerksDto {
	private RiotGamesMatchPerkStatsDto statPerks;
	private List<RiotGamesMatchPerkStyleDto> styles;

}
