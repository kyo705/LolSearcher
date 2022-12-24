package com.lolsearcher.model.entity.champion.enemy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class ChampEnemyCompKey implements Serializable {
	private static final long serialVersionUID = -5366055224920056946L;

	@Column(name = "CHAMPION_ID")
	private String championId;
	@Column(name = "SEASON_ID")
	private int seasonId;
	@Column(name = "ENEMY_CHAMPION_ID")
	private String enemyChampionId;
}
