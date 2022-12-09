package com.lolsearcher.model.entity.championstatic.enemy;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@Entity
public class ChampEnemy {
	@EmbeddedId
	private ChampEnemyCompKey ck;
	
	private long wins;
	
	private long losses;
}
