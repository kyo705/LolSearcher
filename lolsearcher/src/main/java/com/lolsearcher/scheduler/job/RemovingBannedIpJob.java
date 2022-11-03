package com.lolsearcher.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lolsearcher.service.ban.LoginIpBanService;

public class RemovingBannedIpJob implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private LoginIpBanService loginIpBanService;
	
	public RemovingBannedIpJob(LoginIpBanService loginIpBanService) {
		this.loginIpBanService = loginIpBanService;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("스레드 : {} 에서 '{}' 실행", Thread.currentThread(),this.getClass().getName());
		String ip = context.getJobDetail().getJobDataMap().getKeys()[0];
		logger.info(ip);
		loginIpBanService.removeBanList(ip);
	}

}
