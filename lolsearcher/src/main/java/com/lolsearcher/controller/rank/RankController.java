package com.lolsearcher.controller.rank;

import com.lolsearcher.model.input.front.RequestRankDto;
import com.lolsearcher.model.output.front.rank.RankDto;
import com.lolsearcher.service.rank.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class RankController {

    private final RankService rankService;

    @PostMapping("/summoner/rank/old")
    public Map<String, RankDto> getOldRankDto(@RequestBody @Valid RequestRankDto rankInfo){

        return rankService.getOldRanks(rankInfo.getSummonerId());
    }

    @PostMapping("/summoner/rank/renew")
    public Map<String, RankDto> getRecentRankDto(@RequestBody @Valid RequestRankDto rankInfo){

        return rankService.getRenewRanks(rankInfo.getSummonerId());
    }
}
