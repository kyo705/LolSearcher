package com.lolsearcher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class InvalidController {

	@GetMapping("/invalid")
	public ModelAndView invalidParam() {
		return new ModelAndView("error_name");
	}
	
	@GetMapping(path = "/rejected")
	public ModelAndView rejected() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/rejected_ip");
		return mv;
	}
}
