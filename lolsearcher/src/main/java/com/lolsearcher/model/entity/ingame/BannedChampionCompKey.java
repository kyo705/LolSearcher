package com.lolsearcher.model.entity.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class BannedChampionCompKey implements Serializable {
	private static final long serialVersionUID = 9021092936682719458L;
	
	private long gameId;
	
	private int pickTurn;
}
