package com.lolsearcher.model.response.front.match.perk;

import java.util.List;

import lombok.Data;

@Data
public class PerkStyleDto {
	private final String description;
	private final List<PerkStyleSelectionDto> selections;
	private final int style;
}
