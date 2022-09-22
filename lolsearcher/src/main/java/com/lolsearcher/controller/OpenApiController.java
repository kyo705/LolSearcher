package com.lolsearcher.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lolsearcher.service.OpenApiService;

@Controller
public class OpenApiController {

	private final OpenApiService openApiService;
	
	public OpenApiController(OpenApiService openApiService) {
		this.openApiService = openApiService;
	}
	
	@GetMapping(path = "/api/docs/index")
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView("/openapi/manual");
		
		return mv;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GetMapping(path = "/api/docs/summonerid")
	public ModelAndView getSummonerBySummonerId(Model model) {
		ResponseEntity<Map> summoner = (ResponseEntity<Map>) model.getAttribute("summoner");
		
		ModelAndView mv = new ModelAndView("/openapi/summoner-by-id");
		if(summoner!=null) {
			mv.addObject("status", summoner.getStatusCode());
			mv.addObject("header", summoner.getHeaders());
			mv.addObject("summoner", summoner.getBody());
		}
		
		return mv;
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/api/summoner/id")
	public String getSummonerBySummonerId(String summonerid, HttpSession session, RedirectAttributes rttr) {
		String sessionId = session.getId();
		System.out.println("sessionid = "+sessionId);
		ResponseEntity<Map> summoner = openApiService.findSummonerById(summonerid, sessionId);
		
		rttr.addFlashAttribute("summoner", summoner);
		
		return "redirect:/api/docs/summonerid";
	}
	
	
	//---------------------해당 컨트롤러 내 예외 처리 로직-----------------------------------------
	
	@SuppressWarnings("rawtypes")
	@ExceptionHandler(WebClientResponseException.class)
    public String getResponseError(WebClientResponseException e, RedirectAttributes rttr) {
		Map<String, String> map = new HashMap<>();
		map.put("message", e.getMessage());
		
		ResponseEntity<Map> error = new ResponseEntity<Map>(map, e.getHeaders(), e.getStatusCode());
		
		rttr.addFlashAttribute("summoner", error);
		
        return "redirect:/api/docs/summonerid";
    }
}
