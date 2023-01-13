package com.lolsearcher.model.output.front.match;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantDto {

	private String summonerId;
	private int banChampionId;
	private int pickChampionId;
	private short positionId;
	private short championLevel; /* level : 1 ~ 18 */
	private short minionKills; /* lineMinionKills + NeutralMinionKills */
	private short kills;
	private short deaths;
	private short assists;
	private short item0;  /* item 리스트(item0 ~ item6)를 반정규화한 이유 : 1 게임당 70개의 레코드 발생, 아이템의 순서가 중요 */
	private short item1;
	private short item2;
	private short item3;
	private short item4;
	private short item5;
	private short item6;
	private short mainPerk1;
	private short subPerkStyle;
	private ParticipantDetailDto detailDto;
}
