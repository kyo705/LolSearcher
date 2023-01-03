package com.lolsearcher.model.response.front.match.perk;

import com.lolsearcher.model.entity.match.PerkStats;
import lombok.*;

@Data
public class PerkStatsDto {
	private int defense;
	private int flex;
	private int offense;

    public PerkStatsDto(PerkStats perkStats) {
		this.defense = perkStats.getDefense();
		this.flex = perkStats.getFlex();
		this.offense = perkStats.getOffense();
    }
}
