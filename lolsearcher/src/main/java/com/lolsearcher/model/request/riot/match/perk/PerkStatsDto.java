package com.lolsearcher.model.request.riot.match.perk;

import com.lolsearcher.model.entity.match.PerkStats;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerkStatsDto {
	private Short defense;
	private Short flex;
	private Short offense;

    public PerkStats changeToPerkStats() {
		PerkStats perkStats = new PerkStats();

		perkStats.setDefense(defense);
		perkStats.setFlex(flex);
		perkStats.setOffense(offense);

		return perkStats;
    }
}
