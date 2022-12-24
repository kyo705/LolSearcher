package com.lolsearcher.model.entity.champion.item;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@Entity
public class ChampItem {

	@EmbeddedId
	private ChampItemCompKey ck;
	
	private long wins;
	
	private long losses;
}
