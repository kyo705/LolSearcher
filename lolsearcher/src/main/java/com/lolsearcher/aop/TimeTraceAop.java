package com.lolsearcher.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeTraceAop {

	private  final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Around("execution(* com.lolsearcher..*(..)) "
			+ "&& !target(com.lolsearcher.configuration.LolSearcherConfig) "
			+ "&& !target(com.lolsearcher.configuration.SecurityConfig) "
			+ "&& !target(com.lolsearcher.filter.IpBanFilter) "
			+ "&& !target(com.lolsearcher.filter.LolsearcherFilter))")
	public Object execute(ProceedingJoinPoint joinpoint) throws Throwable {
		long start = System.currentTimeMillis();
		log.info("START : '{}'", joinpoint.toShortString());
		
		try {
			return joinpoint.proceed();
		} finally {
			long end = System.currentTimeMillis();
			long total_time = end - start;
			
			log.info("END : '{}' {} ms", joinpoint.toShortString(), total_time);
		}
	}
}
