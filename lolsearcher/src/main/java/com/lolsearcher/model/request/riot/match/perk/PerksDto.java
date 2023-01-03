package com.lolsearcher.model.request.riot.match.perk;

import com.lolsearcher.model.entity.match.Perks;
import lombok.Data;

import java.util.List;

@Data
public class PerksDto {
	private PerkStatsDto statPerks;
	private List<PerkStyleDto> styles;

	public Perks changeToPerks() {
		Perks perks = new Perks();

		perks.setMainPerkStyle(styles.get(0).getStyle());
		perks.setSubPerkStyle(styles.get(1).getStyle());

		perks.setMainPerk1(styles.get(0).getSelections().get(0).getPerk());
		perks.setMainPerk1Var1(styles.get(0).getSelections().get(0).getVar1());
		perks.setMainPerk1Var2(styles.get(0).getSelections().get(0).getVar2());
		perks.setMainPerk1Var3(styles.get(0).getSelections().get(0).getVar3());

		perks.setMainPerk2(styles.get(0).getSelections().get(1).getPerk());
		perks.setMainPerk1Var1(styles.get(0).getSelections().get(1).getVar1());
		perks.setMainPerk2Var2(styles.get(0).getSelections().get(1).getVar2());
		perks.setMainPerk2Var3(styles.get(0).getSelections().get(1).getVar3());

		perks.setMainPerk3(styles.get(0).getSelections().get(2).getPerk());
		perks.setMainPerk3Var1(styles.get(0).getSelections().get(2).getVar1());
		perks.setMainPerk3Var2(styles.get(0).getSelections().get(2).getVar2());
		perks.setMainPerk3Var3(styles.get(0).getSelections().get(2).getVar3());

		perks.setMainPerk4(styles.get(0).getSelections().get(3).getPerk());
		perks.setMainPerk4Var1(styles.get(0).getSelections().get(3).getVar1());
		perks.setMainPerk4Var2(styles.get(0).getSelections().get(3).getVar2());
		perks.setMainPerk4Var3(styles.get(0).getSelections().get(3).getVar3());

		perks.setSubPerk1(styles.get(1).getSelections().get(0).getPerk());
		perks.setSubPerk1Var1(styles.get(1).getSelections().get(0).getVar1());
		perks.setSubPerk1Var2(styles.get(1).getSelections().get(0).getVar2());
		perks.setSubPerk1Var3(styles.get(1).getSelections().get(0).getVar3());

		perks.setSubPerk2(styles.get(1).getSelections().get(1).getPerk());
		perks.setSubPerk2Var1(styles.get(1).getSelections().get(1).getVar1());
		perks.setSubPerk2Var2(styles.get(1).getSelections().get(1).getVar2());
		perks.setSubPerk2Var3(styles.get(1).getSelections().get(1).getVar3());

		return perks;
	}
}
