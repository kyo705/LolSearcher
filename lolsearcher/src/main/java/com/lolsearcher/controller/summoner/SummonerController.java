package com.lolsearcher.controller.summoner;

import com.lolsearcher.model.response.front.summoner.SummonerDto;
import com.lolsearcher.model.request.front.RequestSummonerDto;
import com.lolsearcher.service.summoner.SummonerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lolsearcher.constant.LolSearcherConstants.REGEX;

@RequiredArgsConstructor
@RestController
public class SummonerController {

	private final SummonerService summonerService;
	
	@PostMapping(path = "/summoner")
	public SummonerDto getSummoner(@RequestBody @Valid RequestSummonerDto summonerInfo) {

		validate(summonerInfo);

		return summonerService.getSummonerDto(summonerInfo);
	}

	private void validate(RequestSummonerDto summonerInfo) {

		String unfilteredName = summonerInfo.getSummonerName();
		summonerInfo.setSummonerName(unfilteredName.replaceAll(REGEX, ""));
	}
}
