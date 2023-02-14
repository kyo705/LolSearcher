package com.lolsearcher.exception.handler.join;

import com.lolsearcher.controller.user.JoinController;
import com.lolsearcher.exception.exception.join.CertificationTimeOutException;
import com.lolsearcher.exception.exception.join.RandomNumDifferenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(assignableTypes = JoinController.class)
public class JoinExceptionHandler {

	@ExceptionHandler(CertificationTimeOutException.class)
    public ModelAndView getCertificationTimeOutError(CertificationTimeOutException e) {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("/user/certification_timeout");
		return mv;
    }
	
	@ExceptionHandler(RandomNumDifferenceException.class)
    public ModelAndView getRandomNumDifferenceError(RandomNumDifferenceException e) {
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
