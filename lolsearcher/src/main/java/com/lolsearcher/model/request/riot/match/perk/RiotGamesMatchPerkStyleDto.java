package com.lolsearcher.model.request.riot.match.perk;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RiotGamesMatchPerkStyleDto {
	private String description;
	private List<RiotGamesMatchPerkStyleSelectionDto> selections;
	private short style;
}
