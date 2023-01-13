package com.lolsearcher.model.input.riot.match;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RiotGamesMatchMetadataDto {
    private String dataVersion;
    private String matchId;
    private List<String> participants;
}
