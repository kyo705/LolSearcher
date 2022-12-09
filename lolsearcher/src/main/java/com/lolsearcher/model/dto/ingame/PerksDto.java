package com.lolsearcher.model.dto.ingame;

import lombok.Data;

import java.util.List;

@Data
public class PerksDto {
	private List<Long> perkIds;
	private long perkStyle;
	private long perkSubStyle;
}
