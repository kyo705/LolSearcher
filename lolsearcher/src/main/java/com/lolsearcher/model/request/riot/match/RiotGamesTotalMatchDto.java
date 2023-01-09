package com.lolsearcher.model.request.riot.match;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiotGamesTotalMatchDto {
    private RiotGamesMatchMetadataDto metadata;
    private RiotGamesMatchDto info;
}
