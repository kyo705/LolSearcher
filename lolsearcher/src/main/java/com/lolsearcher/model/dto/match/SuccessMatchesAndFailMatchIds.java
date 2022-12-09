package com.lolsearcher.model.dto.match;

import java.util.List;

import com.lolsearcher.model.entity.match.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SuccessMatchesAndFailMatchIds {
	private List<Match> matches;
	private List<String> failMatchIds;
}
