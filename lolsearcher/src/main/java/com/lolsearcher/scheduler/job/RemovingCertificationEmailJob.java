package com.lolsearcher.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lolsearcher.service.join.JoinService;

public class RemovingCertificationEmailJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private JoinService joinService;
	
	public RemovingCertificationEmailJob(JoinService joinService) {
		this.joinService = joinService;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("스레드 : {} 에서 '{}' 실행", Thread.currentThread(),this.getClass().getName());
		
		String email = context.getJobDetail().getJobDataMap().getKeys()[0];
		joinService.removeRandomNumber(email);
		joinService.removeUncertificatedUser(email);
	}

}
