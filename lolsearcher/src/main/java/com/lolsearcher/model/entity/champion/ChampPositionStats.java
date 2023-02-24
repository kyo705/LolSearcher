package com.lolsearcher.model.entity.champion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@Data
@Entity
@Table(indexes = {@Index(columnList = "gameVersion, positionId, championId")})
public class ChampPositionStats {

	@Id
	private long id;
	private String gameVersion;
	private int championId;
	private int positionId;
	private long wins;
	private long losses;
	private long bans;
}
