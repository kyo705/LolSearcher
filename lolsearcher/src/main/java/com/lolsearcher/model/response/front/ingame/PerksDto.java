package com.lolsearcher.model.response.front.ingame;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class PerksDto implements Serializable {

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
}
