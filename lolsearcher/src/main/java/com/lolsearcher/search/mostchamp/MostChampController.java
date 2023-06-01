package com.lolsearcher.search.mostchamp;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.lolsearcher.search.mostchamp.MostChampConstant.MOST_CHAMPS_URI;

@RequiredArgsConstructor
@RestController
public class MostChampController {

    private final MostChampService mostChampService;

    @GetMapping(MOST_CHAMPS_URI)
    public List<MostChampDto> getMostChampList(@ModelAttribute @Valid MostChampRequest request){

        return mostChampService.getMostChamps(request);
    }
}
