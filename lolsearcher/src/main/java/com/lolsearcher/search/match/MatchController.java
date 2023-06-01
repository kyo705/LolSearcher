package com.lolsearcher.search.match;

import com.lolsearcher.search.match.dto.MatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/summoner/{summonerId}/match")
    public List<MatchDto> getOldMatches(@ModelAttribute @Valid MatchRequest request){

        return matchService.findMatches(request);
    }
}
