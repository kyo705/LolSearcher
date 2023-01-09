package com.lolsearcher.controller.rank;

import com.lolsearcher.model.request.front.RequestRankDto;
import com.lolsearcher.model.response.front.rank.RankDto;
import com.lolsearcher.service.rank.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class RankController {

    private final RankService rankService;

    @PostMapping("/summoner/rank")
    public Map<String, RankDto> getRankDto(@RequestBody @Valid RequestRankDto rankInfo){

        String summonerId = rankInfo.getSummonerId();
        boolean renew = rankInfo.isRenew();

        if(!renew) {
            return rankService.getOldRanks(summonerId);
        }
        try {
            return rankService.getRenewRanks(summonerId);
        }catch(DataIntegrityViolationException e) {
            return rankService.getOldRanks(summonerId);
        }
    }
}
