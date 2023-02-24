package com.lolsearcher.controller.search.match;

import com.lolsearcher.model.request.search.match.RequestMatchDto;
import com.lolsearcher.model.response.front.search.match.MatchDto;
import com.lolsearcher.service.search.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/summoner/match/old")
    public List<MatchDto> getOldMatches(@RequestBody @Valid RequestMatchDto request){

        return matchService.getMatchesInDB(request);
    }
}
