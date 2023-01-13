package com.lolsearcher.model.output.front.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Data
public class InGameBannedChampionDto implements Serializable {

	private int pickTurn;
	private long championId;
	private long teamId;
}
