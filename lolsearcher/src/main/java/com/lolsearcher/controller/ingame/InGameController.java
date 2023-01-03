package com.lolsearcher.controller.ingame;

import com.lolsearcher.model.response.front.ingame.InGameDto;
import com.lolsearcher.service.ingame.InGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
public class InGameController {

	private final InGameService inGameService;
	
	@PostMapping(path = "/summoner/ingame")
	public InGameDto inGame(@RequestBody String summonerId) {

		InGameDto inGameDto = inGameService.getInGame(summonerId);

		return inGameDto;
	}
}
