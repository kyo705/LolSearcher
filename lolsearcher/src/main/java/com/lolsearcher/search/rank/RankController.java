package com.lolsearcher.search.rank;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.lolsearcher.search.rank.RankConstant.FIND_RANKS_URI;
import static com.lolsearcher.search.rank.RankConstant.FIND_RANK_BY_ID_URI;

@RequiredArgsConstructor
@RestController
public class RankController {

    private final RankService rankService;

    @GetMapping(FIND_RANKS_URI)
    public Map<RankTypeState, RankDto> findAll(@ModelAttribute @Validated(RankFindAllGroup.class) RankRequest request) {

        return rankService.findAllById(request);
    }

    @GetMapping(FIND_RANK_BY_ID_URI)
    public Map<RankTypeState, RankDto> findOneById(@ModelAttribute @Validated(RankFindByIdGroup.class) RankRequest request) {

        return rankService.findOneById(request);
    }
}
