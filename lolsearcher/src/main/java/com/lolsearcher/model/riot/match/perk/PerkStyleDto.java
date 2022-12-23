package com.lolsearcher.model.riot.match.perk;

import lombok.Data;

import java.util.List;

@Data
public class PerkStyleDto {
	private String description;
	private List<PerkStyleSelectionDto> selections;
	private short style;
}
