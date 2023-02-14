package com.lolsearcher.controller.search.rank;

import com.lolsearcher.model.request.RequestRankDto;
import com.lolsearcher.model.response.front.rank.RankDto;
import com.lolsearcher.service.search.rank.RankService;
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
}
