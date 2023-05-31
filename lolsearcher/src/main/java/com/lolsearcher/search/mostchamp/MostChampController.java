package com.lolsearcher.search.mostchamp;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MostChampController {

    private final MostChampService mostChampService;

    @GetMapping("/summoner/{summonerId}/most-champ")
    public List<MostChampDto> getMostChampList(@Validated @ModelAttribute MostChampRequest request){

        return mostChampService.getMostChamps(request);
    }
}
