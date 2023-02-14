package com.lolsearcher.model.entity.champion;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@Table(indexes = {@Index(columnList = "gameVersion, championId, itemId")})
public class ChampItemStats {

	@Id
	private long id;
	private String gameVersion;
	private int championId;
	private int itemId;
	private long wins;
	private long losses;
}
