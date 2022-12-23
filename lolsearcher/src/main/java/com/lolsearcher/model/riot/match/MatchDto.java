package com.lolsearcher.model.riot.match;

import com.lolsearcher.model.entity.match.Match;
import lombok.Data;

@Data
public class MatchDto {
    private MetadataDto metadata;
    private InfoDto info;

    public Match changeToMatch(){
        Match match = new Match();
        match.setMatchId(metadata.getMatchId());
        match.setGameDuration(info.getGameDuration());
        match.setGameEndTimestamp(info.getGameEndTimestamp());
        match.setQueueId(info.getQueueId());

        return match;
    }
}
