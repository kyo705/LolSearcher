package com.lolsearcher.exception.handler;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.controller.SummonerController;
import com.lolsearcher.service.ban.SearchIpBanService;

@ControllerAdvice(assignableTypes = SummonerController.class)
public class SummonerExceptionHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final SearchIpBanService searchIpBanService;
	
	public SummonerExceptionHandler(SearchIpBanService searchIpBanService) {
		this.searchIpBanService = searchIpBanService;
	}
	
	@ExceptionHandler(WebClientResponseException.class)
    public ModelAndView getResponseError(WebClientResponseException e, ServletRequest req) {
		ModelAndView mv = new ModelAndView();
		
		logger.error("'{}' error occurred by 'Riot' game server", e.getStatusCode().value());
		
		if(e.getStatusCode().equals(HttpStatus.BAD_GATEWAY)||
			e.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)||
			e.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)||
			e.getStatusCode().equals(HttpStatus.GATEWAY_TIMEOUT)) {
			//500번대 에러
			mv.setViewName("error/game_server");
			return mv;
		}
		if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)||
				e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
			mv.setViewName("/error/name");
		}else if(e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
			mv.setViewName("/error/manyreq");
		}
		String user_ip = req.getRemoteAddr();
		if(searchIpBanService.isExceedBanCount(user_ip)) {
			searchIpBanService.registerBanList(user_ip);
			logger.error(" ip : '{}' user is banned because of too many bad request", user_ip);
			mv.setViewName("rejected_ip");
		}
        return mv;
    }
	
	@ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView getOverlapSavingEntityError(DataIntegrityViolationException e) {
		logger.error("DB에 중복 데이터 저장 예외가 발생");
		
        return new ModelAndView("/error/overlap");
    }
}
