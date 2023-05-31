package com.lolsearcher.search.rank;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class RankController {

    private final RankService rankService;

    @GetMapping({"/summoner/{summonerId}/ranks"})
    public Map<RankTypeState, RankDto> findAll(@ModelAttribute @Validated(RankFindAllGroup.class) RankRequest request) {

        return rankService.findAllById(request);
    }

    @GetMapping({"/summoner/{summonerId}/rank/{rankId}"})
    public Map<RankTypeState, RankDto> findOneById(@ModelAttribute @Validated(RankFindByIdGroup.class) RankRequest request) {

        return rankService.findOneById(request);
    }
}
