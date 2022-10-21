package com.lolsearcher.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;;

@ControllerAdvice
public class LolSearcherExceptionHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
}
