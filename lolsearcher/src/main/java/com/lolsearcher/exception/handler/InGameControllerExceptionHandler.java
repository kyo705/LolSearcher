package com.lolsearcher.exception.handler;

import com.lolsearcher.controller.InGameController;
import com.lolsearcher.exception.ingame.MoreInGameException;
import com.lolsearcher.exception.ingame.NoInGameException;
import com.lolsearcher.exception.summoner.MoreSummonerException;
import com.lolsearcher.exception.summoner.NoSummonerException;
import com.lolsearcher.model.dto.summoner.SummonerDto;
import com.lolsearcher.service.summoner.SummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice(assignableTypes = InGameController.class)
public class InGameControllerExceptionHandler {

    private final SummonerService summonerService;

    @ExceptionHandler({WebClientResponseException.class, NoInGameException.class, MoreInGameException.class})
    public ModelAndView getNoInGameError(Exception e, ServletRequest req) {
        String name = (String) req.getAttribute("name");

        log.error(e.getMessage());
        log.info("'{}'는 현재 게임 중이 아닙니다.", name);

        SummonerDto summonerDto = summonerService.findOldSummoner(name);

        ModelAndView mv = new ModelAndView();
        mv.addObject("summoner", summonerDto);
        mv.setViewName("error/ingame");

        return mv;
    }

    @ExceptionHandler({NoSummonerException.class, MoreSummonerException.class})
    public ModelAndView getNoSummonerDataError(Exception e, ServletRequest req) {
        String name = (String) req.getAttribute("name");

        log.error(e.getMessage());
        log.info("'{}'는 존재하지 않는 소환사입니다.", name);

        ModelAndView mv = new ModelAndView();
        mv.addObject("name", name);
        mv.setViewName("error/no_summoner");

        return mv;
    }
}
