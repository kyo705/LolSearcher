package com.lolsearcher.model.entity.champion.position;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.persistence.Column;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChampPositionCompKey implements Serializable {
	private static final long serialVersionUID = -6206971371948243388L;
	
	@Column(name = "CHAMPION_ID")
	private String championId;
	@Column(name = "SEASON_ID")
	private int seasonId;
	@Column(name = "POSITION")
	private String position;
}
