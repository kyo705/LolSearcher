package com.lolsearcher.model.response.front.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TotalRankDtos {
	private RankDto solorank;
	private RankDto teamrank;
}
