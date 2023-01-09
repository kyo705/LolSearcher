package com.lolsearcher.model.response.front.ingame;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class InGamePerksDto implements Serializable {

	private short mainPerk1;
	private short mainPerk2;
	private short mainPerk3;
	private short mainPerk4;

	private short subPerk1;
	private short subPerk2;

	private short statPerk1;
	private short statPerk2;
	private short statPerk3;

	private short perkStyle;
	private short perkSubStyle;
}
