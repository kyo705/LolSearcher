package com.lolsearcher.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.lolsearcher.domain.entity.user.LolSearcherUser;
import com.lolsearcher.exception.join.CertificationTimeOutException;
import com.lolsearcher.exception.join.RandomNumDifferenceException;
import com.lolsearcher.service.UserService;

@Controller
public class UserController {
	
	private UserService userService;
	
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(path = "/loginForm")
	public ModelAndView loginFrom(@RequestAttribute(required = false) String loginFailMessage) {
		
		ModelAndView mv = new ModelAndView("/user/login");
		if(loginFailMessage!=null) {
			mv.addObject("loginFailMessage", loginFailMessage);
		}
		
		
		return mv;
	}
	
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
		
		userService.sendCertificationToEmail(username, password, email);
		
		mv.addObject("email", email);
		mv.setViewName("/user/self_certification");
		return mv;
	}
	
	@PostMapping(path = "/selfCertification")
	public ModelAndView selfCertification(@RequestParam Integer number,
										@RequestParam String email) {
		ModelAndView mv = new ModelAndView();
		
		LolSearcherUser user = userService.certificate(email, number);
		userService.joinUser(user);
		
		mv.setViewName("/user/join_success");
		
		return mv;
	}
	
	
	@PostMapping(path = "/findId")
	public ModelAndView findExistedId(@RequestParam String userid) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("userid", userid);
		
		if(!userService.isPossibleForm(userid)) {
			mv.addObject("joinPossible", 2);
			mv.setViewName("redirect:/joinForm");
			return mv;
		}
		
		if(!userService.isExistedId(userid)) {
			mv.addObject("joinPossible", 0);
		}else {
			mv.addObject("joinPossible", 1);
		}
		
		mv.setViewName("redirect:/joinForm");
		
		
		return mv;
	}
	
	@GetMapping(path = "/expired")
	public ModelAndView sessionExpired() {
		
		return new ModelAndView("/user/session_expired");
	}
	
	
	//-------------------해당 컨트롤러 예외 처리 메소드--------------------------
	
	@ExceptionHandler(CertificationTimeOutException.class)
    public ModelAndView getCertificationTimeOutError(CertificationTimeOutException e) {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("/user/certification_timeout");
		return mv;
    }
	
	@ExceptionHandler(RandomNumDifferenceException.class)
    public ModelAndView getRandomNumdifferenceError(RandomNumDifferenceException e) {
		ModelAndView mv = new ModelAndView();
		String email = e.getEmail();
		
		mv.addObject("email", email);
		mv.addObject("failMessage", "인증 번호가 틀립니다. 다시 입력해주세요");
		//ip 주소로 실패 카운트 세고 특정 횟수 초과 시 ip차단 OR email 사용불가
		mv.setViewName("/user/self_certification");
		return mv;
    }
	
	@ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView getAccountExistError(DataIntegrityViolationException e) {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("/user/joined_already");
		return mv;
    }
}
