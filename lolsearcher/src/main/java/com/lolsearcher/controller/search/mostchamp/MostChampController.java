package com.lolsearcher.controller.search.mostchamp;

import com.lolsearcher.model.request.search.RequestMostChampDto;
import com.lolsearcher.model.response.front.mostchamp.ResponseMostChampDto;
import com.lolsearcher.service.search.mostchamp.MostChampService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class MostChampController {

    private final MostChampService mostChampService;

    @PostMapping("/summoner/most-champ")
    public List<ResponseMostChampDto> getMostChampList(@RequestBody @Valid RequestMostChampDto request){

        return mostChampService.getMostChamps(request);
    }
}
