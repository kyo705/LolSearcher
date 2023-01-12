package com.lolsearcher.controller.match;

import com.lolsearcher.model.response.front.match.MatchDto;
import com.lolsearcher.model.request.front.RequestMatchDto;
import com.lolsearcher.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/summoner/match")
    public List<MatchDto> getMatches(@RequestBody @Valid RequestMatchDto matchInfo){

        int totalSize = matchInfo.getCount();
        List<MatchDto> matches = new ArrayList<>(totalSize);

        if(matchInfo.isRenew()) {
            matches.addAll(matchService.getApiMatches(matchInfo)); //최대 20개 match 데이터 가져옴
        }

        matchInfo.setCount(matchInfo.getCount() - matches.size());

        if(matchInfo.getCount() > 0){
            matches.addAll(matchService.getDbMatches(matchInfo));
        }

        return sortAndResizeMatches(matches, totalSize);
    }

    private List<MatchDto> sortAndResizeMatches(List<MatchDto> matches, int size) {
        matches.sort((a,b)->{
            if(a.getGameEndTimestamp()-b.getGameEndTimestamp()>0){
                return -1;
            }
            if(a.getGameEndTimestamp()-b.getGameEndTimestamp()<0){
                return 1;
            }
            return 0;
        });

        return matches.size() > size ? (matches.subList(0, size)) : matches;
    }
}
