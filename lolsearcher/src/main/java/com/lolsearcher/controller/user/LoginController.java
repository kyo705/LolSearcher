package com.lolsearcher.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
	@RequestMapping(path = "/loginForm")
	public ModelAndView loginFrom(@RequestAttribute(required = false) String loginFailMessage) {
		ModelAndView mv = new ModelAndView("/user/login");
		if(loginFailMessage!=null) {
			mv.addObject("loginFailMessage", loginFailMessage);
		}
		return mv;
	}
}
