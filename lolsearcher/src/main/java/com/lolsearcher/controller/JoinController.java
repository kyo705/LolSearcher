package com.lolsearcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.model.dto.user.JoinStatus;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.service.join.JoinService;

@RequiredArgsConstructor
@Controller
public class JoinController {
	private final JoinService userService;
	
	@GetMapping(path = "/joinForm")
	public ModelAndView joinForm(@RequestParam(required = false) String userid,
								@RequestParam(required = false) Integer joinPossible) {
		ModelAndView mv = new ModelAndView("/user/join");
		if(userid!=null) {
			mv.addObject("userid", userid);
			mv.addObject("joinPossible", joinPossible);
		}
		return mv;
	}
	
	
	@PostMapping(path = "/findId")
	public ModelAndView findExistedId(@RequestParam String userid) {
		ModelAndView mv = new ModelAndView("redirect:/joinForm");
		mv.addObject("userid", userid);
		
		if(!userService.isValidForm(userid)) {
			mv.addObject("joinPossible", JoinStatus.NOTALLOWED.getValue());
			return mv;
		}
		if(userService.findUserByUsername(userid) != null) {
			mv.addObject("joinPossible", JoinStatus.EXISTED.getValue());
			return mv;
		}
		mv.addObject("joinPossible", JoinStatus.OK.getValue());
		return mv;
	}
	
	
	@PostMapping(path = "/join")
	public ModelAndView join(@RequestParam String username,
							@RequestParam String password,
							@RequestParam String email) {
		ModelAndView mv = new ModelAndView();
		LolSearcherUser user = userService.findUserByEmail(email);
		if(user!=null) {
			mv.setViewName("/user/joined_already");
			return mv;
		}
		userService.sendCertificationEmail(username, password, email);
		
		mv.addObject("email", email);
		mv.setViewName("/user/self_certification");
		return mv;
	}
	
	
	@PostMapping(path = "/selfCertification")
	public ModelAndView selfCertification(@RequestParam Integer certificationNumber,
										@RequestParam String email) {
		LolSearcherUser user = userService.certificate(email, certificationNumber);
		userService.joinUser(user);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/user/join_success");
		return mv;
	}
	
	
	@GetMapping(path = "/expired")
	public ModelAndView sessionExpired() {
		
		return new ModelAndView("/user/session_expired");
	}
}
