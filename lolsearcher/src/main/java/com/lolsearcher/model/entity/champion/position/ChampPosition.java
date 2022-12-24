package com.lolsearcher.model.entity.champion.position;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;


@Data
@Entity
public class ChampPosition {
	@EmbeddedId
	private ChampPositionCompKey ck;
	
	private long wins;
	
	private long losses;
}
