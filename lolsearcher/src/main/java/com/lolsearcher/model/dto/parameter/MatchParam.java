package com.lolsearcher.model.dto.parameter;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class MatchParam {
	private String name;
	private String summonerId;
	private String champion;
	private int gameType;
	private int count;
}
