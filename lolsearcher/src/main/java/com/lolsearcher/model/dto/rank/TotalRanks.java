package com.lolsearcher.model.dto.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TotalRanks {
	private RankDto solorank;
	private RankDto teamrank;
}
