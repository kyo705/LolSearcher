package com.lolsearcher.controller.mostchamp;

import com.lolsearcher.model.response.front.mostchamp.ResponseMostChampDto;
import com.lolsearcher.model.request.front.RequestMostChampDto;
import com.lolsearcher.service.mostchamp.MostChampService;
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
    public List<ResponseMostChampDto> getMostChampList(@RequestBody @Valid RequestMostChampDto mostChampInfo){

        return mostChampService.getMostChamps(mostChampInfo);
    }
}
