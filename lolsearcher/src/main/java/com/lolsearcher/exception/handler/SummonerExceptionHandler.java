package com.lolsearcher.exception.handler;

import javax.servlet.ServletRequest;

import com.lolsearcher.exception.match.InCorrectPerksDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.controller.SummonerController;
import com.lolsearcher.service.ban.SearchIpBanService;

@Slf4j
@ControllerAdvice(assignableTypes = SummonerController.class)
public class SummonerExceptionHandler {
	
	private final SearchIpBanService searchIpBanService;
	
	public SummonerExceptionHandler(SearchIpBanService searchIpBanService) {
		this.searchIpBanService = searchIpBanService;
	}
	
	@ExceptionHandler(WebClientResponseException.class)
    public ModelAndView getResponseError(WebClientResponseException e, ServletRequest request) {
		ModelAndView mv = new ModelAndView();

		log.error("'{}' error occurred by 'Riot' game server", e.getStatusCode().value());

		if (e.getStatusCode() == HttpStatus.BAD_GATEWAY ||
				e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR ||
				e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE ||
				e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT
		) {
			//500번대 에러
			mv.setViewName("error/game_server");
			return mv;
		}

		if (e.getStatusCode().equals(HttpStatus.NOT_FOUND) ||
				e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
			mv.setViewName("/error/name");
		} else if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
			mv.setViewName("/error/manyreq");
		}

		String userIp = request.getRemoteAddr();
		if (checkIpBan(userIp)) {
			mv.setViewName("rejected_ip");
		}

		return mv;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView getOverlapSavingEntityError(DataIntegrityViolationException e) {
		log.error("DB에 중복 데이터 저장 예외가 발생");
		
        return new ModelAndView("/error/overlap");
    }

	@ExceptionHandler(InCorrectPerksDataException.class)
	public ModelAndView getInCorrectPerksDataInDBError(InCorrectPerksDataException e) {
		log.error("DB 내 Perks 데이터 개체 무결성 위배 => 데이터가 없거나 중복되어있음");

		return new ModelAndView("/error/server");
	}

	private boolean checkIpBan(String userIp) {
		if(searchIpBanService.isExceedBanCount(userIp)) {
			searchIpBanService.registerBanList(userIp);
			log.error(" ip : '{}' user is banned because of too many bad request", userIp);

			return true;
		}
		return false;
	}
}
