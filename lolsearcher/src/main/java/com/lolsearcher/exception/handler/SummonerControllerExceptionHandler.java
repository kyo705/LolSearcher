package com.lolsearcher.exception.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.controller.SummonerController;
import com.lolsearcher.exception.SameNameExistException;
import com.lolsearcher.filter.IpBanFilter;

@ControllerAdvice(assignableTypes = SummonerController.class)
public class SummonerControllerExceptionHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final Map<String, Integer> banCount;
	private final ApplicationContext appContext;
	
	public SummonerControllerExceptionHandler(ApplicationContext appContext) {
		banCount = new ConcurrentHashMap<String, Integer>();
		this.appContext = appContext;
	}
	
	@ExceptionHandler(SameNameExistException.class)
    public ModelAndView getSameSummonerExistError(SameNameExistException e, ServletRequest req) {
		//로그 기록
		logger.error(e.getMessage());
		
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("server_error");
		
		return mv;
	}
	
	@ExceptionHandler(WebClientResponseException.class)
    public ModelAndView getResponseError(WebClientResponseException e, ServletRequest req) {
		ModelAndView mv = new ModelAndView();
		
		logger.error("'{}' error occurred by 'Riot' game server", e.getStatusCode());
		
		if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)||
				e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
			mv.setViewName("error_name");
			
		}else if(e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
			mv.setViewName("error_manyreq");
			
		}else if(e.getStatusCode().equals(HttpStatus.BAD_GATEWAY)||
				e.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)||
				e.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)||
				e.getStatusCode().equals(HttpStatus.GATEWAY_TIMEOUT)) {
			//500번대 에러
			mv.setViewName("error_game_server");
			
			return mv;
		}
		
		String user_ip = req.getRemoteAddr();
		
		banCount.put(user_ip, banCount.getOrDefault(user_ip, 0)+1);
		
		if(banCount.get(user_ip)>=30) {
			IpBanFilter banFilter = appContext.getBean(IpBanFilter.class);
			banFilter.addBanList(user_ip);
			
			logger.error(" ip : '{}' user is banned because of too many bad request", user_ip);
			mv.setViewName("rejected_ip");
			banCount.remove(user_ip);
			
			return mv;
		}
		
		
        return mv;
    }
}
