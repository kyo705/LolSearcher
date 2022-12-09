package com.lolsearcher.model.dto.parameter;

import lombok.*;

@Builder
@AllArgsConstructor
@Data
public class MostChampParam {
	private String summonerId;
	private int gameQueue;
	private int season;
}

