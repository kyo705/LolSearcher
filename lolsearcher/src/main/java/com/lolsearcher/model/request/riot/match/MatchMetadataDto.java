package com.lolsearcher.model.request.riot.match;

import lombok.Data;

import java.util.List;

@Data
public class MatchMetadataDto {
    private String dataVersion;
    private String matchId;
    private List<String> participants;
}
