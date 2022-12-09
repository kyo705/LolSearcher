package com.lolsearcher.model.dto.match.perk;


import com.lolsearcher.model.entity.match.Perks;
import lombok.Data;

@Data
public class PerksDto {
	private PerkStatsDto statPerks;

	private short mainPerkStyle;
	private short subPerkStyle;

	private short mainPerk1;
	private short mainPerk1Var1;
	private short mainPerk1Var2;
	private short mainPerk1Var3;

	private short mainPerk2;
	private short mainPerk2Var1;
	private short mainPerk2Var2;
	private short mainPerk2Var3;

	private short mainPerk3;
	private short mainPerk3Var1;
	private short mainPerk3Var2;
	private short mainPerk3Var3;

	private short mainPerk4;
	private short mainPerk4Var1;
	private short mainPerk4Var2;
	private short mainPerk4Var3;

	private short subPerk1;
	private short subPerk1Var1;
	private short subPerk1Var2;
	private short subPerk1Var3;

	private short subPerk2;
	private short subPerk2Var1;
	private short subPerk2Var2;
	private short subPerk2Var3;

	public PerksDto(Perks perks) {
		this.statPerks = new PerkStatsDto(perks.getPerkStats());

		this.mainPerkStyle = perks.getMainPerkStyle();
		this.subPerkStyle = perks.getSubPerkStyle();

		this.mainPerk1 =  perks.getMainPerk1();
		this.mainPerk1Var1 =  perks.getMainPerk1Var1();
		this.mainPerk1Var2 =  perks.getMainPerk1Var2();
		this.mainPerk1Var3 =  perks.getMainPerk1Var3();

		this.mainPerk2 =  perks.getMainPerk2();
		this.mainPerk2Var1 =  perks.getMainPerk2Var1();
		this.mainPerk2Var2 =  perks.getMainPerk2Var2();
		this.mainPerk2Var3 =  perks.getMainPerk2Var3();

		this.mainPerk3 =  perks.getMainPerk3();
		this.mainPerk3Var1 =  perks.getMainPerk3Var1();
		this.mainPerk3Var2 =  perks.getMainPerk3Var2();
		this.mainPerk3Var3 =  perks.getMainPerk3Var3();

		this.mainPerk4 =  perks.getMainPerk4();
		this.mainPerk4Var1 =  perks.getMainPerk4Var1();
		this.mainPerk4Var2 =  perks.getMainPerk4Var2();
		this.mainPerk4Var3 =  perks.getMainPerk4Var3();

		this.subPerk1 =  perks.getSubPerk1();
		this.subPerk1Var1 =  perks.getSubPerk1Var1();
		this.subPerk1Var2 =  perks.getSubPerk1Var2();
		this.subPerk1Var3 =  perks.getSubPerk1Var3();

		this.subPerk2 =  perks.getSubPerk2();
		this.subPerk2Var1 =  perks.getSubPerk2Var1();
		this.subPerk2Var2 =  perks.getSubPerk2Var2();
		this.subPerk2Var3 =  perks.getSubPerk2Var3();
	}
}
