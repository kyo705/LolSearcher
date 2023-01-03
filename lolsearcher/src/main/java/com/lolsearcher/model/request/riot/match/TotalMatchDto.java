package com.lolsearcher.model.request.riot.match;

import com.lolsearcher.model.entity.match.Match;
import lombok.Data;

@Data
public class TotalMatchDto {
    private MatchMetadataDto metadata;
    private MatchDto info;

    public Match changeToMatch(){
        Match match = new Match();
        match.setMatchId(metadata.getMatchId());
        match.setGameDuration(info.getGameDuration());
        match.setGameEndTimestamp(info.getGameEndTimestamp());
        match.setQueueId(info.getQueueId());

        return match;
    }
}
