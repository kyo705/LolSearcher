package com.lolsearcher.model.dto.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Data
public class BannedChampionDto implements Serializable {

	private int pickTurn;
	private long championId;
	private long teamId;
}
