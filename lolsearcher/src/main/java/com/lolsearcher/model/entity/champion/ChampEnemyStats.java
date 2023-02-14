package com.lolsearcher.model.entity.champion;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@Table(indexes = {@Index(columnList = "gameVersion, championId, enemyChampionId")})
public class ChampEnemyStats {

	@Id
	private long id;
	private String gameVersion;
	private int championId;
	private int enemyChampionId;
	private long wins;
	private long losses;
}
