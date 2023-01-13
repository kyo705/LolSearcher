package com.lolsearcher.controller.match;

import com.lolsearcher.model.input.front.RequestMatchDto;
import com.lolsearcher.model.output.front.match.MatchDto;
import com.lolsearcher.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/summoner/match/old")
    public List<MatchDto> getOldMatches(@RequestBody @Valid RequestMatchDto request){

        return matchService.getDbMatches(request);
    }


    @PostMapping(value = "/summoner/match/renew" , produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MatchDto> getRecentMatches(@RequestBody @Valid RequestMatchDto requestMatchDto){

        return matchService.getApiMatchesFlux(requestMatchDto); //최대 20개 match 데이터 가져옴
    }
}
